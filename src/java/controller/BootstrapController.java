package controller;

import dao.TableManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseConnectionManager;

/**
 * BootstrapController class is used to contain methods in calling out individual CSV controllers and combining output results
 * @author Jiacheng / Jing Xiang
 */
public class BootstrapController {

    /**
     * TableManager is used to drop and create tables 
     */
    private TableManager tableManager;
    /**
     * LinkedHashMap of combinedMap is used to store final results after combining individual LinkedHashMaps
     */
    private LinkedHashMap combinedMap;
    /**
     * DemographicsCSVController is used for validating demographics csv and inserting data
     */
    private DemographicsCSVController demographicsCSVController;
    /**
     * LocationCSVController is used for validating location csv and inserting data
     */
    private LocationCSVController locationCSVController;
    /**
     * LocationLLCSVController is used for validating location-lookup csv and inserting data
     */
    private LocationLLCSVController locationLLCSVController;

    /**
     * Constructor of the BootstrapController class Instantiate tableManager,
     * demographicsCSVController, locationCSVController and
     * locationLLCSVController
     */
    public BootstrapController() {
        tableManager = new TableManager();
        demographicsCSVController = new DemographicsCSVController();
        locationCSVController = new LocationCSVController();
        locationLLCSVController = new LocationLLCSVController();
    }

    /**
     * Gets Bootstrap results after processing the files inside the given
     * repository directory
     *
     * @param fileDir Directory of the repository
     * @return LinkedHashMap validation results of the uploaded zip file
     */
    public LinkedHashMap processBootstrap(String fileDir) {
        //This variable is used to check if this is a additional file upload or bootstrap
        //If location-lookup.csv is found, this will be set to true
        boolean bootstrap = false;

        // set up LinkedHashMap to hold all the validations for "status", "num-record-loaded" and "error" (if any)
        combinedMap = new LinkedHashMap<String, Object>();
        combinedMap.put("status", "error");
        combinedMap.put("num-record-loaded", new ArrayList<LinkedHashMap<String, Object>>());
        combinedMap.put("error", new ArrayList<LinkedHashMap<String, Object>>());

        try {
            // Instantiate File for each CSV which holds the reference of its file path 
            // a File.separator is used as a "divider" to differentiate across different OS
            File demographicsFile = new File(fileDir + File.separator + "demographics.csv");
            File locationFile = new File(fileDir + File.separator + "location.csv");
            File locationLookupFile = new File(fileDir + File.separator + "location-lookup.csv");

            // check if locationLookupFile exists - if it exists, it means that it is bootstrap and not uploading of additional csv
            if (locationLookupFile.exists()) {
                // drop and create tables
                tableManager.dropAllTables();
                tableManager.createAllTables();
                // bootstrap in progress
                bootstrap = true;
            } else {
                if (!demographicsFile.exists()) {
                    throw new FileNotFoundException("demographics.csv not found");
                }
                if (!locationFile.exists()) {
                    throw new FileNotFoundException("location.csv not found");
                }
            }

            // instantiate individual LinkedHashMap of results for each csv
            LinkedHashMap<String, Object> locationLookupMap = null;
            LinkedHashMap<String, Object> locationMap = null;
            LinkedHashMap<String, Object> demographicsMap = null;

            // instantiating a Connection object to be used.
            Connection conn = null;
            try {
                // get Connection from DatabaseConnectionManager and throws SQLException if it fails
                conn = DatabaseConnectionManager.getConnection();
            } catch (SQLException ex) {
                Logger.getLogger(BootstrapController.class.getName()).log(Level.SEVERE, null, ex);
            }

            // if it is bootstrap, we need to validate location-lookup csv
            if (bootstrap) {
                locationLookupMap = locationLLCSVController.validateLocationLookupFile(locationLookupFile, conn);
            }
            if (locationFile.exists()) {
                locationMap = locationCSVController.validateLocationFile(locationFile, conn);
            }
            if (demographicsFile.exists()) {
                demographicsMap = demographicsCSVController.validateDemographicsFile(demographicsFile, conn);
            }

            // close the Connection object
            DatabaseConnectionManager.closeConnection(conn);

            // Combine all num-record-loaded entries from individual maps into numRecordList
            ArrayList<LinkedHashMap<String, Object>> numRecordList = (ArrayList<LinkedHashMap<String, Object>>) combinedMap.get("num-record-loaded");

            numRecordList.add(((ArrayList<LinkedHashMap<String, Object>>) demographicsMap.get("num-record-loaded")).get(0));

            // getting the num-record-loaded for location-lookup map if it is bootstrap
            if (bootstrap) {
                numRecordList.add(((ArrayList<LinkedHashMap<String, Object>>) locationLookupMap.get("num-record-loaded")).get(0));
            }

            numRecordList.add(((ArrayList<LinkedHashMap<String, Object>>) locationMap.get("num-record-loaded")).get(0));

            // Combine all error entries from individual maps into errorList
            ArrayList<LinkedHashMap<String, Object>> errorList = (ArrayList<LinkedHashMap<String, Object>>) combinedMap.get("error");

            // check for null is for cases where the individual map does not contain any error
            if (demographicsMap.get("error") != null) {
                ArrayList<LinkedHashMap<String, Object>> demographicsErrorList = (ArrayList<LinkedHashMap<String, Object>>) demographicsMap.get("error");
                errorList.addAll(demographicsErrorList);
            }
            if (bootstrap && locationLookupMap.get("error") != null) {
                ArrayList<LinkedHashMap<String, Object>> locationLookupErrorList = (ArrayList<LinkedHashMap<String, Object>>) locationLookupMap.get("error");
                errorList.addAll(locationLookupErrorList);
            }
            if (locationMap.get("error") != null) {
                ArrayList<LinkedHashMap<String, Object>> locationErrorList = (ArrayList<LinkedHashMap<String, Object>>) locationMap.get("error");
                errorList.addAll(locationErrorList);
            }

            // if there is not error, we would want to change the status to success and remove the error field
            if (errorList.isEmpty()) {
                combinedMap.put("status", "success");
                combinedMap.remove("error");
            }

            // Delete all csv files after processing
            if (demographicsFile.exists()) {
                demographicsFile.delete();
            }
            if (locationFile.exists()) {
                locationFile.delete();
            }
            if (bootstrap) {
                locationLookupFile.delete();
            }
        } catch (FileNotFoundException e) {
            System.out.println("******" + e.getMessage() + "******");
        }
        return combinedMap;
    }
}
