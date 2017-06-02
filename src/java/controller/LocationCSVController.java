package controller;

import au.com.bytecode.opencsv.CSVReader;
import dao.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import utility.CSVValidation;
import utility.ErrorMessage;
import utility.Validation;

/**
 * LocationCSVController contains methods in validating the location csv file and inserting them into database if valid
 * @author Jiacheng / Jing Xiang
 */
public class LocationCSVController {
    
    /**
     * LocationDAO to be used to create, retrieve data from database
     */
    private LocationDAO locationDAO;
    
     /**
     * Constructor of LocationLLCSVController <br>
     * Instantiate LocationLookupDAO to be used
     */
    public LocationCSVController() {
        locationDAO = new LocationDAO();
    }
    /**
     * Process the location file uploaded
     *
     * @param locationFile location file - "location.csv"
     * @param conn connection to be reused throughout
     * @return LinkedHashMap validation result of the location
     */
    public LinkedHashMap validateLocationFile(File locationFile, Connection conn) {
        
        // Instantiate a LinkedHashMap to store the locationLookupResult
        LinkedHashMap locationResult = new LinkedHashMap<String, Object>();
        
        // Set up locationResult to hold status
        locationResult.put("status", "success");

        // Set up locationResult to hold num-record-loaded
        ArrayList<LinkedHashMap<String, Object>> numRecordList = new ArrayList<LinkedHashMap<String, Object>>();
        locationResult.put("num-record-loaded", numRecordList);

        // Set up a TreeMap of errorList to hold duplicatedrow and messageList
        TreeMap<Integer, Object> errorList = new TreeMap<Integer, Object>();

        // Instantiate the LinkedHashMap for to store the number of record loaded
        LinkedHashMap<String, Object> fileNumRecordLoad = new LinkedHashMap<String, Object>();

        //Created by Kenneth to check the own csv internally for duplicates
        LinkedHashMap<String, Integer> checkForDuplicates = new LinkedHashMap<String, Integer>();
        LinkedHashMap<Integer, LinkedHashMap<String, String>> valuesToAdd = new LinkedHashMap<Integer, LinkedHashMap<String, String>>();

        //Created by Kenneth to store up to 50000 values
        ArrayList<LinkedHashMap<String, String>> toCreateValues = new ArrayList<LinkedHashMap<String, String>>();

        //Date date1 = new Date();
        //System.out.println("Start:" + date1.toString());
        try {
            // instantiate the CSVReader to read the location csv file
            CSVReader reader = new CSVReader(new FileReader(locationFile));
            
            String[] nextLine = null;             // instantiate a nextLine string array to store a line
            String[] header = reader.readNext();  // skip the header row
            int lineNum = 2;                     // line number will start at 2  
            int noOfSuccessfulRow = 0;          // initialize the number of successfulrow from top
            String timeStamp = "";
            String macAddress = "";
            String locationId = "";

            // looping through per line using CSVReader 
            while ((nextLine = reader.readNext()) != null) {
                // instantiate the revisedLine after the initial line has been trimmed of blank space in front and behind
                String[] revisedLine = CSVValidation.trimFields(nextLine);
                
                // Return an Integer ArrayList of errors due to blank fields (the integer corresponds to the respective order in the header row) 
                ArrayList<Integer> errors = CSVValidation.validateBlankFields(revisedLine);
                
                // Instantiate the messagelist to store validation errors
                ArrayList<String> messageList = new ArrayList<String>();
                
                // if there is blank errors we would just want to add them to messageList and not going to further validation
                if (!errors.isEmpty()) {
                    // for each of the integer that correspond to the order of header row, we retrieve the string of fieldheader and add to messagelist
                    for (Integer error : errors) {
                        String fieldHeader = header[error];
                        messageList.add(fieldHeader + " is blank");
                    }
                } else {
                    // there are no blank errors in the else loop, and we would want to go into further validation
                    
                    // validate locationid (different from validateLocationLookupFile()'s!!!)
                    locationId = revisedLine[2];
                    if (!Validation.validateExistingLocationId(conn, locationId)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidLocation"));
                    }

                    // validate mac address (used in validateDemographicsFile())
                    macAddress = revisedLine[1];
                    if (!Validation.validateMacAddress(macAddress)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidMacAddress"));
                    }
                    // validate timestamp
                    timeStamp = revisedLine[0];
                    if (!Validation.validateTimeStamp(timeStamp)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidTimestamp"));
                    }
                    
                    //Let's check from the database if there are any duplicate records
                    if (messageList.isEmpty()) {
                        int duplicateRow = Validation.validateDuplicateLocation(conn, timeStamp, macAddress, Integer.parseInt(locationId), lineNum);
                    
                        //If there is a duplicate row from the database previously added from this file
                        if (duplicateRow > 0) {
                                //The DAO has already updated the current row number

                                //Now let's add an error message for the earlier duplicate error
                                ArrayList<String> errorMessagesList = new ArrayList<String>();
                                errorMessagesList.add(ErrorMessage.getMsg("duplicateRow"));
                                errorList.put(duplicateRow, errorMessagesList);

                        } else if (duplicateRow == 0) {
                            //There is a duplicate row from previous records
                            messageList.add(ErrorMessage.getMsg("duplicateRow"));
                        } else {
                            //There are no duplicates from the database.. fine, but let's check the current timestamp
                            Object duplicateRecord = checkForDuplicates.get(timeStamp + "-" + macAddress);

                            if (duplicateRecord != null) {

                                int duplicateLineNum = (Integer) duplicateRecord;
                                ArrayList<String> errorMessagesList = new ArrayList<String>();
                                errorMessagesList.add(ErrorMessage.getMsg("duplicateRow"));
                                errorList.put(duplicateLineNum, errorMessagesList);
                                valuesToAdd.remove(duplicateLineNum);
                                checkForDuplicates.put(timeStamp + "-" + macAddress, lineNum);

                            } else {
                                checkForDuplicates.put(timeStamp + "-" + macAddress, lineNum);
                            }
                        }
                    }
                }
                
                // if messageList is empty at the end, we will add the line into database
                if (messageList.isEmpty()) {
                    LinkedHashMap<String, String> rowToAdd = new LinkedHashMap<String, String>();
                    rowToAdd.put("rowId", String.valueOf(lineNum));
                    rowToAdd.put("time_stamp", timeStamp);
                    rowToAdd.put("mac_address", macAddress);
                    rowToAdd.put("location_id", locationId);
                    valuesToAdd.put(lineNum, rowToAdd);

                } else {
                    errorList.put(lineNum, messageList);
                }
                lineNum++;

                if (valuesToAdd.size() == 50000) {
                    for (Map.Entry<Integer, LinkedHashMap<String, String>> entry : valuesToAdd.entrySet()) {
                        toCreateValues.add(entry.getValue());
                    }
                    Date currentDate = new Date();
                    System.out.println("Execute: " + toCreateValues.size() + " queries - Current Date:" + currentDate.toString());
                    locationDAO.batchCreate(conn, toCreateValues);
                    noOfSuccessfulRow += toCreateValues.size();
                    toCreateValues.clear();
                    valuesToAdd.clear();
                    checkForDuplicates.clear();
                }
            }

            if (valuesToAdd.size() > 0) {
                for (Map.Entry<Integer, LinkedHashMap<String, String>> entry : valuesToAdd.entrySet()) {
                    toCreateValues.add(entry.getValue());
                }
                Date currentDate = new Date();
                System.out.println("Execute: " + toCreateValues.size() + " queries - Current Date:" + currentDate.toString());
                locationDAO.batchCreate(conn, toCreateValues);
                noOfSuccessfulRow += toCreateValues.size();
                toCreateValues.clear();
                valuesToAdd.clear();
                checkForDuplicates.clear();
            }

            locationDAO.setAllLocationRecordsToZero(conn);
            // add fileNumRecordLoad to the overall numRecordList
            fileNumRecordLoad.put("location.csv", noOfSuccessfulRow);
            numRecordList.add(fileNumRecordLoad);
            reader.close();

        } catch (IOException ioe) {
        }

        //If error list is not empty, that means we have an error, and we would want to remove the error field
        if (errorList.size() > 0) {
            ArrayList<LinkedHashMap<String,Object>> finalErrorList = new ArrayList<LinkedHashMap<String,Object>>();
            //ArrayList<JsonObject> finalErrorList = new ArrayList<JsonObject>();
            for (Map.Entry<Integer, Object> entry : errorList.entrySet()) {
                //FileValidationError fve = new FileValidationError("location.csv", entry.getKey(), (ArrayList) entry.getValue());
                
                 LinkedHashMap<String,Object> error = new LinkedHashMap<String, Object>();
                 
                 // if messageList is not empty, we would want to log the line that has the error
                 error.put("file", "location.csv");
                 error.put("line", entry.getKey());
                 error.put("message", (ArrayList) entry.getValue());                
                 
                finalErrorList.add(error);
            }
            errorList.clear();
            locationResult.put("status", "error");
            locationResult.put("error", finalErrorList);
        }
        //Date date2 = new Date();
        //System.out.println("End:" + date2.toString());

        return locationResult;
    }
}
