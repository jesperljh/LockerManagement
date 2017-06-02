package controller;

import au.com.bytecode.opencsv.CSVReader;
import dao.*;
import utility.CSVValidation;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import utility.ErrorMessage;
import utility.Validation;

/**
 * DemographicsCSVController contains methods in validating the demographics csv file and inserting them into database if valid
 * @author Jiacheng / Jing Xiang
 */
public class DemographicsCSVController {

    /**
     * DemographicsDAO to be used to create, retrieve data from database
     */
    private DemographicsDAO demographicsDAO;
    
     /**
     * Constructor of DemographicsCSVController
     * Instantiate DemographicsDAO to be used
     */
    public DemographicsCSVController() {
        demographicsDAO = new DemographicsDAO();
    }
    
    /**
     * Process the demographicsFile uploaded
     *
     * @param demographicsFile demographics file - "demographics.csv"
     * @param conn connection to be reused throughout
     * @return LinkedHashMap validation result of the demographicsfile
     */
    public LinkedHashMap validateDemographicsFile(File demographicsFile, Connection conn) {
        
        // Instantiate a LinkedHashMap to store the locationLookupResult
        LinkedHashMap demographicsResult = new LinkedHashMap<String, Object>();
        
        // Set up demographicsResult to hold status
        demographicsResult.put("status", "success");

        // Set up demographicsResult to hold num-record-loaded
        ArrayList<LinkedHashMap<String, Object>> numRecordList = new ArrayList<LinkedHashMap<String, Object>>();
        demographicsResult.put("num-record-loaded", numRecordList);

        ArrayList<LinkedHashMap<String, Object>> errorList = new ArrayList<LinkedHashMap<String, Object>>();

        LinkedHashMap<String, Object> fileNumRecordLoad = new LinkedHashMap<String, Object>();

        try {
            // instantiate the CSVReader to read the demographics csv file
            CSVReader reader = new CSVReader(new FileReader(demographicsFile));
            
            String[] nextLine = null;           // instantiate a nextLine string array to store a line
            String[] header = reader.readNext();  // skip the header row
            int lineNum = 2;                    // line number will start at 2  
            int noOfSuccessfulRow = 0;          // initialize the number of successfulrow from top
            
            // looping through per line using CSVReader 
            while ((nextLine = reader.readNext()) != null) {
                
                // instantiate a LinkedHashMap to hold all the error founds in a row
                LinkedHashMap<String, Object> errorRow = new LinkedHashMap<String, Object>();
                
                // instantiate the revisedLine after the initial line has been trimmed of blank space in front and behind
                String[] revisedLine = CSVValidation.trimFields(nextLine);
                
                // Return an Integer ArrayList of errors due to blank fields (the integer corresponds to the respective order in the header row) 
                ArrayList<Integer> blankErrors = CSVValidation.validateBlankFields(revisedLine);
                
                // Instantiate the messagelist to store validation errorss\
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
                    
                    // validate mac-address
                    String macAddress = revisedLine[0];
                    if (!Validation.validateMacAddress(macAddress)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidMacAddress"));
                    }
                    // validate password
                    String password = revisedLine[2];
                    if (!Validation.validatePassword(password)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidPassword"));
                    }
                    // validate email
                    String email = revisedLine[3];
                    if (!Validation.validateEmail(email)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidEmail"));
                    }
                    // validate gender
                    String gender = revisedLine[4];
                    if (!Validation.validateGender(gender)) {
                        // retrieve the corresepond error message from ErrorMessage class
                        messageList.add(ErrorMessage.getMsg("invalidGender"));
                    }
                }
                
                // if messageList is empty at the end, we will add the line into database
                if (messageList.isEmpty()) {
                    demographicsDAO.create(conn, revisedLine[0], revisedLine[1], revisedLine[2], revisedLine[3], revisedLine[4].toUpperCase());
                    noOfSuccessfulRow++;
                } else {
                    // if messageList is not empty, we would want to log the line that has the error
                    errorRow.put("file", "demographics.csv");
                    errorRow.put("line", lineNum);
                    errorRow.put("message", messageList);
                    errorList.add(errorRow);
                }
                lineNum++;
            }
        // add fileNumRecordLoad to the overall numRecordList
        fileNumRecordLoad.put("demographics.csv", noOfSuccessfulRow);
        numRecordList.add(fileNumRecordLoad);
        reader.close();
        } catch (IOException ioe) {

        }
        
        //If error list is not empty, that means we have an error, and we would want to remove the error field
        if (errorList.size() > 0) {
            demographicsResult.put("status", "error");
            demographicsResult.put("error", errorList);
        }
        return demographicsResult;
    }
}
