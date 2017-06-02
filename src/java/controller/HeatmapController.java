package controller;

import dao.LocationLookupDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;

/**
 * HeatmapController is used to retrieve heatmap results
 *
 * @author Eugene
 */
public class HeatmapController {

    LocationLookupDAO locationLookupDAO;

    /**
     * Constructor for HeatmapController. Initializes LocationLookupDAO.
     */
    public HeatmapController() {
        // instantiate locationLookupDAO
        locationLookupDAO = new LocationLookupDAO();
    }

    /**
     * Gets Heatmap data, based on given floor and dateTime.
     *
     * @param floor Floor to be requested
     * @param dateTime Date and Time to be requested
     * @param token Token to be requested
     * @param fromJson To check if request is from webservice or UI. If it's from webservice, only admins are allowed to access.
     * @return LinkedHashMap of Heatmap data with given floor and dateTime
     */
    public LinkedHashMap getHeatmap(String floor, String dateTime, String token, boolean fromJson) {

        //Declare LinkedHashMap to be returned as results
        LinkedHashMap<String, Object> heatmapResult = new LinkedHashMap<String, Object>();

        //Declare and initialize messages to store any form of error messages
        ArrayList<String> messages = new ArrayList<String>();

        //set floorNumber to 0
        int floorNumber = 0;

        //Set secondDateTime (15 minutes later)
        String secondDateTime = null;

        // checks for missing floor input
        if (floor == null) {
            messages.add(ErrorMessage.getMsg("missingFloor"));

            // checks for empty floor input
        } else if (floor.length() == 0) {
            messages.add(ErrorMessage.getMsg("blankFloor"));
        } else {
            try {
                // converting of floor String into an integer, will throw NumberFormatException if not numeric
                floorNumber = Integer.parseInt(floor);

                // checks for valid floor (betwwen 0(inclusive) to 5(inclusive))
                if (floorNumber < 0 || floorNumber > 5) {
                    messages.add(ErrorMessage.getMsg("invalidFloor"));
                }

            } catch (NumberFormatException ex) {
                messages.add(ErrorMessage.getMsg("invalidFloor"));

            }
        }
        //check for empty datetime, or null datetime
        if (dateTime == null) {

            //Check for missing date
            messages.add(ErrorMessage.getMsg("missingDate"));
        } else if (dateTime.length() == 0) {

            //Check for blank date
            messages.add(ErrorMessage.getMsg("blankDate"));

        } //check for valid date
        else {
            //At this stage, the format is assumed to have a T, and it is not empty.

            //Finally, let's try to parse the datetime to see if the format is valid
            if (!DateValidation.validateDateFormat(dateTime)) {
                messages.add(ErrorMessage.getMsg("invalidDate"));
            }
        }
        
        
        // Validate token
        if (token == null) {
            messages.add(ErrorMessage.getMsg("missingToken"));
        } else if (token.isEmpty()) {
            messages.add(ErrorMessage.getMsg("blankToken"));
        } else {
            if (fromJson) {
                if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                    messages.add(ErrorMessage.getMsg("invalidToken"));
                }
            } else {
                if (!TokenValidation.validateToken(token)) {
                    messages.add(ErrorMessage.getMsg("invalidToken"));
                }
            }
        }
        
        //If messages size is bigger than 0, means there is an error, and we should display error messages
        if (messages.size() > 0) {

            // sort the error message list according to alphabetical order
            Collections.sort(messages);
            heatmapResult.put("status", "error");
            heatmapResult.put("messages", messages);

            //Return immediately, as there is no longer a need to process further
            return heatmapResult;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        //Declare arrayList of LinkedHashMap to store heatmap data
        ArrayList<LinkedHashMap> heatmapList = new ArrayList<LinkedHashMap>();
        
        // Get the start date time of the processing window by removing the 'T'
        dateTime = dateTime.replace('T', ' ');
        
        //Get the datetime 15 minutes before
        secondDateTime = DateTimeUtility.getTimeBefore(dateTime, 15);

        //Parse in parameters to the DAO to retrieve it.
        LinkedHashMap<String, Object> locationList = locationLookupDAO.getHeatmap(floorNumber, secondDateTime, dateTime);

        //Iterate through the LinkedHashMap to get the semantic Place and the crowd Number
        for (Map.Entry<String, Object> entry : locationList.entrySet()) {
            //Declare LinkedHashMap to store specific heatmap data (semantic-place, num-people, crowd-density)
            LinkedHashMap<String, Object> locationMap = new LinkedHashMap<String, Object>();

            //Insert value of name into semantic-place
            locationMap.put("semantic-place", entry.getKey());

            //Get number of people in the specific look up
            int noOfPeople = (Integer) entry.getValue();

            //Insert value of number of people into num-people
            locationMap.put("num-people", new Integer(noOfPeople));

            //Assigning specific crowd density index base on number of people
            if (noOfPeople == 0) {
                locationMap.put("crowd-density", new Integer(0));
            } else if (noOfPeople <= 2) {
                locationMap.put("crowd-density", new Integer(1));
            } else if (noOfPeople <= 5) {
                locationMap.put("crowd-density", new Integer(2));
            } else if (noOfPeople <= 10) {
                locationMap.put("crowd-density", new Integer(3));
            } else if (noOfPeople <= 20) {
                locationMap.put("crowd-density", new Integer(4));
            } else if (noOfPeople <= 30) {
                locationMap.put("crowd-density", new Integer(5));
            } else {
                locationMap.put("crowd-density", new Integer(6));
            }
            // Storing of heatmap data of each location look up into the arrayList 
            heatmapList.add(locationMap);

        }
        // Storing of status result into hash map to be returned
        heatmapResult.put("status", "success");

        // Storing of heatmap result into hash map to be returned
        heatmapResult.put("heatmap", heatmapList);

        //Return results
        return heatmapResult;
    }
}
