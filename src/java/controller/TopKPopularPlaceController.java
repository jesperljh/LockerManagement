package controller;

import dao.LocationDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;

/**
 * TopKPopularPlaceCountroller class contains methods that will be used to
 * retrieve the top k number of popular places
 *
 * @author Jiacheng/Jocelyn
 */
public class TopKPopularPlaceController {

    /**
     * Retrieves a LinkedHashMap of Top K number of Popular Places within a time span
     * @return LinkedHashMap Returns a LinkedHashMap of Top K Popular Places Results consisting of rank, semantic-place and the number of people at the semantic place.
     * @param endDateTime Specifies the time span window end date time to retrieve the Top K Popular Places
     * @param token Specifies the token, to be validated
     * @param k The number of rank of popular places to be displayed.
     * @param fromJson This will be set to true if it is called by the Json Web Services. If it is true, then token validation will ensure it comes from the admin.
     */
    public LinkedHashMap<String, Object> getTopKPopularPlace(String k, String endDateTime, String token, boolean fromJson) {

        // Declare LinkedHashMap to be returned as results
        LinkedHashMap<String, Object> linkedHashMapResult = new LinkedHashMap<String, Object>();

        // Declare and initialize messages to store any form of error messages
        ArrayList<String> messages = new ArrayList<String>();

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

        // Set k number as the default value 3
        int kNumber = 0;

        // Set startDateTime (15 minutes later)
        String startDateTime = null;

        // Checks for non-null and non-empty k
        if (k == null) {
            kNumber = 3;
        } else if (k.length() == 0) {
            kNumber = 3;
        } else {
            try {

                // Converting of k String into an integer, will throw NumberFormatException if not numeric
                kNumber = Integer.parseInt(k);

                // Checks for valid k (between 1(inclusive) to 10(inclusive))
                if (kNumber < 1 || kNumber > 10) {
                    messages.add(ErrorMessage.getMsg("invalidK"));
                }

            } catch (NumberFormatException ex) {
                messages.add(ErrorMessage.getMsg("invalidK"));

            }
        }

        // Check for empty endDateTime, or null endDateTime
        if (endDateTime == null) {

            //Check for missing date
            messages.add(ErrorMessage.getMsg("missingDate"));
        } else if (endDateTime.length() == 0) {

            //Check for blank date
            messages.add(ErrorMessage.getMsg("blankDate"));

        } // Check for valid date
        else {

            // At this stage, the format is assumed to have a T, and it is not empty.
            // Validate endDateTime to have the correct format
            if (!DateValidation.validateDateFormat(endDateTime)) {
                messages.add(ErrorMessage.getMsg("invalidDate"));
            } else {

                // endDateTime is of correct format, replace T with white space.
                endDateTime = endDateTime.replace('T', ' ');

                // Retrieve a startDateTime of 15 minutes before.
                startDateTime = DateTimeUtility.getTimeBefore(endDateTime, 15);
            }
        }

        // If messages size is bigger than 0, means there is an error, and we should display error messages
        if (messages.size() > 0) {

            // Sort errors according to alphabetical order
            Collections.sort(messages);

            // Put status as "error"
            linkedHashMapResult.put("status", "error");

            // Put messages as messages
            linkedHashMapResult.put("messages", messages);

            // Return immediately, as there is no longer a need to process further
            return linkedHashMapResult;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        // Declare ArrayList of LinkedHashMap to store results to be returned
        ArrayList<LinkedHashMap<String, Object>> topKPopularPlaceList = new ArrayList<LinkedHashMap<String, Object>>();

        // Initialize locationDAO to retrieve results
        LocationDAO locationDAO = new LocationDAO();

        // Parse in parameters to the DAO to retrieve it.
        TreeMap<Integer, ArrayList<String>> topKPopularPlaceMap = locationDAO.getTopKPopularPlace(startDateTime, endDateTime);

        // Put rank into each popularPlaceMap
        int rank = 1;
        for (Map.Entry<Integer, ArrayList<String>> entry : topKPopularPlaceMap.entrySet()) {
            int semanticPlaceCount = entry.getKey();
            ArrayList<String> semanticPlaceList = entry.getValue();
            for (String semanticPlace : semanticPlaceList) {
                LinkedHashMap<String, Object> popularPlace = new LinkedHashMap<String, Object>();
                popularPlace.put("rank", rank);
                popularPlace.put("semantic-place", semanticPlace);
                popularPlace.put("count", semanticPlaceCount);
                topKPopularPlaceList.add(popularPlace);
            }
            rank++;
            if (rank > kNumber) {
                break;
            }
        }

        // Storing of status result into link hash map to be returned
        linkedHashMapResult.put("status", "success");

        // Storing of topKPopularPlace result into hash map to be returned
        linkedHashMapResult.put("results", topKPopularPlaceList);

        // return results
        return linkedHashMapResult;
    }
}
