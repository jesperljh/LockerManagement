package controller;

import au.com.bytecode.opencsv.CSVReader;
import dao.LocationLookupDAO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import utility.CSVValidation;
import utility.ErrorMessage;
import utility.Validation;

/**
 * LocationLLCSVController contains methods in validating the location-lookup csv file and inserting them into database if valid
 * @author Jiacheng / Jing Xiang
 */
public class LocationLLCSVController {

    /**
     * LocationLookupDAO to be used to create, retrieve data from database
     */
    private LocationLookupDAO locationLookupDAO;
    
    /**
     * Constructor of LocationLLCSVController
     * Instantiate LocationLookupDAO to be used
     */
    public LocationLLCSVController() {
        locationLookupDAO = new LocationLookupDAO();
    }
    
    /**
     * Process the locationlookup file uploaded
     *
     * @param locationLookupFile locationlookup file - "location-lookup.csv"
     * @param conn connection to be reused throughout
     * @return LinkedHashMap validation result of the locationlookup
     */
    public LinkedHashMap validateLocationLookupFile(File locationLookupFile, Connection conn) {
        
        // Instantiate a LinkedHashMap to store the locationLookupResult
        LinkedHashMap locationLookupResult = new LinkedHashMap<String, Object>();
        
//        // Set up locationLookupResult to hold status
        locationLookupResult.put("status", "success");
        
        // Set up locationLookupResult to hold num-record-loaded
        ArrayList<LinkedHashMap<String, Object>> numRecordList = new ArrayList<LinkedHashMap<String, Object>>();
        locationLookupResult.put("num-record-loaded", numRecordList);
        
        // Instantiate an Arraylist of error to store failed validation 
        ArrayList<LinkedHashMap<String, Object>> errorList = new ArrayList<LinkedHashMap<String, Object>>();

        // Instantiate a LinkedHashMap to hold the num record load
        LinkedHashMap<String, Object> fileNumRecordLoad = new LinkedHashMap<String, Object>();

        try {
            // instantiate the CSVReader to read the locationlookup csv file
            CSVReader reader = new CSVReader(new FileReader(locationLookupFile));
            
            String[] nextLine = null;               // instantiate a nextLine string array to store a line
            String[] header = reader.readNext();  // skip the header row
            int lineNum = 2; // line number will start at 2
            int noOfSuccessfulRow = 0; // initialize the number of successfulrow from top
            
            // looping through per line using CSVReader 
            while ((nextLine = reader.readNext()) != null) {
                
                // instantiate a LinkedHashMap to hold all the error founds in a row
                LinkedHashMap<String, Object> errorRow = new LinkedHashMap<String, Object>();
                
                // instantiate the revisedLine after the initial line has been trimmed of blank space in front and behind
                String[] revisedLine = CSVValidation.trimFields(nextLine);
                
                // Return an Integer ArrayList of errors due to blank fields (the integer corresponds to the respective order in the header row) 
                ArrayList<Integer> blankErrors = CSVValidation.validateBlankFields(revisedLine);
                
                // Instantiate the messagelist to store validation errors
                ArrayList<String> messageList = new ArrayList<String>();

                // if there is blank errors we would just want to add them to messageList and not going to further validation
                if (!blankErrors.isEmpty()) {
                    // for each of the integer that correspond to the order of header row, we retrieve the string of fieldheader and add to messagelist
                    for (Integer error : blankErrors) {
                        String fieldHeader = header[error];
                        messageList.add(fieldHeader + " is blank");
                    }
                } else {
                    // there are no blank errors in the else loop, and we would want to go into further validation
                    
                    // validate locationid
                    String locationId = revisedLine[0];
                    if (!Validation.validateLocationId(locationId)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidLocationId"));
                    }
                    // validate Semantic Place
                    String semanticPlace = revisedLine[1];
                    if (!Validation.validateSemanticPlace(semanticPlace)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidSemanticPlace"));
                    }
                }
                
                // if messageList is empty at the end, we will add the line into database
                if (messageList.isEmpty()) {
                    locationLookupDAO.create(conn, Integer.parseInt(revisedLine[0]), revisedLine[1]);
                    noOfSuccessfulRow++;
                } else {
                    // if messageList is not empty, we would want to log the line that has the error
                    errorRow.put("file", "location-lookup.csv");
                    errorRow.put("line", lineNum);
                    errorRow.put("message", messageList);

                    errorList.add(errorRow);
                }
                lineNum++;
            }
            // add fileNumRecordLoad to the overall numRecordList
            fileNumRecordLoad.put("location-lookup.csv", noOfSuccessfulRow);
            numRecordList.add(fileNumRecordLoad);
            reader.close();
        } catch (IOException ioe) {
            System.out.println("Fail to read file" + ioe.getMessage());
        }

        //If error list is not empty, that means we have an error, and we would want to remove the error field
        if (errorList.size() > 0) {
            locationLookupResult.put("status", "error");
            locationLookupResult.put("error", errorList);
        }
        return locationLookupResult;
    }
}
