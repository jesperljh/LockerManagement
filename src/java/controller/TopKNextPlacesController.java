package controller;

import dao.LocationDAO;
import dao.LocationLookupDAO;
import entity.Location;
import entity.LocationLookup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;
import utility.TopKUtility;

/**
 * Top K Next Places Controller returns the top k next places based on a semantic place
 * @author Eugene/Jocelyn
 */
public class TopKNextPlacesController {

    private LocationDAO locationDAO;
    private LocationLookupDAO locationLookupDAO;

    /**
     * Gets TopKNextPlaces data, based on given specified origin, dateTime and rank
     *
     * @param origin origin semantic place to be requested
     * @param dateTime Date and Time to be requested
     * @param k rank to be requested
     * @param token to be validated
     * @param fromJson to be validated
     *
     * @return LinkedHashMap of TopKNextPlaces data with given origin, dateTime,
     * and rank
     */
    public LinkedHashMap getTopKNextPlaces(String origin, String dateTime, String k, String token, boolean fromJson) {
        //instantiate LinkedHashMap to store final result to return
        LinkedHashMap<String, Object> topKNextPlacesResult = new LinkedHashMap<String, Object>();

        //instantiate ArrayList to store error messages
        ArrayList<String> errorMessageList = new ArrayList<String>();

        //instantiate ArrayList to store results of final companion list to be stored in topKCompanionResult
        ArrayList<LinkedHashMap> resultList = new ArrayList<LinkedHashMap>();

        //instantiate HashMap to store final nextVisitedPlace of each user if exist and total-non-next-place-users and total-next-place-users records
        HashMap<String, Integer> semanticPlaceAndUserRecord = new HashMap<String, Integer>();

        //initialize totalNextPlaceUsers to 0 which will be updated later to keep track of users that travelled from origin to an eligible next visited place
        semanticPlaceAndUserRecord.put("totalNextPlaceUsers", 0);

        //initialize totalNonNextPlaceUsers to 0 which will be updated in later stages to keep track of users that has not travelled to any eligible next visited place
        semanticPlaceAndUserRecord.put("totalNonNextPlaceUsers", 0);

        //instantiate TreeMap to be used to sort user time spent according to rank
        TreeMap<Integer, ArrayList<String>> userTimeSpentSorter = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

        //instantiate locationLookupDAO
        locationLookupDAO = new LocationLookupDAO();

        //instantiate locationDAO
        locationDAO = new LocationDAO();

        // Validate token
        if (token == null) {
            errorMessageList.add(ErrorMessage.getMsg("missingToken"));
        } else if (token.isEmpty()) {
            errorMessageList.add(ErrorMessage.getMsg("blankToken"));
        } else {
            if (fromJson) {
                if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                    errorMessageList.add(ErrorMessage.getMsg("invalidToken"));
                }
            } else {
                if (!TokenValidation.validateToken(token)) {
                    errorMessageList.add(ErrorMessage.getMsg("invalidToken"));
                }
            }
        }
        
        // Check for empty datetime, or null datetime
        if (dateTime == null) {

            //Check for missing date
            errorMessageList.add(ErrorMessage.getMsg("missingDate"));
        } else if (dateTime.length() == 0) {

            //Check for blank date
            errorMessageList.add(ErrorMessage.getMsg("blankDate"));

        } else {
            //check for valid date
            if (!DateValidation.validateDateFormat(dateTime)) {
                errorMessageList.add(ErrorMessage.getMsg("invalidDate"));
            }
        }

        //check for empty origin
        if (origin == null) {
            errorMessageList.add(ErrorMessage.getMsg("missingOrigin"));
            //check for blank email
        } else if (origin.length() == 0) {
            errorMessageList.add(ErrorMessage.getMsg("blankOrigin"));
        } else {
            //check for valid origin place
            LocationLookup semanticPlace = locationLookupDAO.retrieve(origin);
            if (semanticPlace == null) {
                errorMessageList.add(ErrorMessage.getMsg("invalidOrigin"));
            }
        }

        //initialize kInt to the default value of 3 for "k"
        int kInt = 3;

        //check if k is empty or null
        if (k != null && k.length() != 0) {
            try {
                //check if k is a valid number
                kInt = Integer.parseInt(k);
                //check if k is between 1 to 10
                if (kInt < 1 || kInt > 10) {
                    errorMessageList.add(ErrorMessage.getMsg("invalidK"));
                }
            } catch (NumberFormatException nfe) {
                errorMessageList.add(ErrorMessage.getMsg("invalidK"));
            }
        }

        //check if error message list is empty. If not empty, we end here
        if (!errorMessageList.isEmpty()) {
            Collections.sort(errorMessageList);
            topKNextPlacesResult.put("status", "error");
            topKNextPlacesResult.put("messages", errorMessageList);
            return topKNextPlacesResult;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        //instantiate startDateTime to store start query window time
        String startDateTime = null;

        //remove "T" from dateTime input 
        dateTime = dateTime.replace("T", " ");

        //Let's store the window start date time into startDateTime variable
        startDateTime = DateTimeUtility.getTimeBefore(dateTime, 15);

        //retrieves list of unique users latest update located at origin between startDateTime and dateTime
        ArrayList<Location> locationList = locationDAO.getOriginUserList(origin, startDateTime, dateTime);
        //if locationList is empty, it means no users at origin. Hence, we return empty result list
        
        //compute nextDateTime where nextDateTime is 15mins from dateTime
        String nextDateTime = DateTimeUtility.getTimeAfter(dateTime, 15);

        //to track number of total unique users at place of origin
        Integer totalUsers = 0;

        if (locationList.size() != 0) {

            //this will store the total number of unique users at origin
            totalUsers = locationList.size();

            for (Location loc : locationList) {

                //get each unique user macAddress to be used for retrieving their full updates within next 15mins window
                String userMacAddress = loc.getMacAddress();
                
                //retrieve all location updates of this macAddress
                ArrayList<ArrayList<Location>> userUpdateList = TopKUtility.getAllLocPairsOfMacAddress(userMacAddress, dateTime, nextDateTime);
                
                //to retrieve this user's next visited place
                //this returned result will be the user's last eligible next visited place that passes the 5mins rule.
                String visitedPlace = TopKUtility.computeVisitedSemanticPlace(userUpdateList);
                //System.out.println(visitedPlace);
                
                if (visitedPlace != null) {
                    //means that user has an eligible next visited place
                    
                    //we check if any other previous users has this user's next visited place as their next visited place
                    Integer count = (Integer) semanticPlaceAndUserRecord.get(visitedPlace);

                    if (count == null) {
                        //means no previous users has this semantic place as their next visited place
                        //hence we set the count to 1 under this semantic place as 1 in semanticPlaceAndUserRecord
                        semanticPlaceAndUserRecord.put(visitedPlace, 1);
                    } else {
                        //we increase the count under this semantic place by 1
                        count++;
                        //and update it in semanticPlaceAndUserRecord
                        semanticPlaceAndUserRecord.put(visitedPlace, count);
                    }
                    //means that user has travelled and we would want to update totalNextPlaceUsers count record
                    int users = semanticPlaceAndUserRecord.get("totalNextPlaceUsers");
                    users++;
                    semanticPlaceAndUserRecord.put("totalNextPlaceUsers", users);

                } else {
                    //means that user has not travelled and we would want to update totalNonNextPlaceUsers count record
                    int users = semanticPlaceAndUserRecord.get("totalNonNextPlaceUsers");
                    users++;
                    semanticPlaceAndUserRecord.put("totalNonNextPlaceUsers", users);
                }

            }
        }

        //we retrieve the finalized totalNextPlaceUsersCount here and remove it to populate all other finalNextVisitedPlace data inside semanticPlaceAndUserRecord
        Integer totalNextPlaceUsersCount = semanticPlaceAndUserRecord.remove("totalNextPlaceUsers");

        //we retrieve the finalized totalNonNextPlaceUsersCount here and remove it to populate all other finalNextVisitedPlace data inside semanticPlaceAndUserRecord
        Integer totalNonNextPlaceUsersCount = semanticPlaceAndUserRecord.remove("totalNonNextPlaceUsers");
        //System.out.println("NONNEXTPLACEUSERS" + totalNonNextPlaceUsersCount);

        // we retrieve all keys of semanticPlaceAndUserRecord to be used for sorting according to specified rank
        Set<String> semanticPlaces = semanticPlaceAndUserRecord.keySet();

        //we would want to iterate through all semantic places inside semanticPlaceAndUserRecord to retrieve count data 
        //and have them ranked according to its count(number of users that share the same next visited place)
        Iterator<String> iter = semanticPlaces.iterator();
        
        while (iter.hasNext()) {

            //retrieve semantic place from the list
            String semanticPlace = iter.next();

            //retrieve the count under this semantic place
            Integer count = semanticPlaceAndUserRecord.get(semanticPlace);

            //retrieve any previous records under same count from userTimeSpentSorter TreeMap if any
            ArrayList<String> spList = userTimeSpentSorter.get(count);

            //check if userTimeSpentSorter has any records of same count
            if (spList == null) {

                //means that no previous count record is found and we would want to instantiate spList to a new list to 
                //store semantic place and have further semantic place that share the same count to be added on to this list
                spList = new ArrayList<String>();
            }

            //we add the semantic place to this list
            spList.add(semanticPlace);

            //update userTimeSpentSorter with the updated semanticPlace arraylist under a unique count
            userTimeSpentSorter.put(count, spList);
        }

        //at this point, we would want to output the results according to the rank specified by the user
        int rank = 1;

        //we retrieve all the keys of userTimeSpentSorter, which in this case is the count
        Set<Integer> countList = userTimeSpentSorter.keySet();
        Iterator<Integer> iter2 = countList.iterator();

        //we iterate through the countList according to the rank that user has specified
        while (iter2.hasNext() && rank <= kInt) {
            Integer count = iter2.next();
            ArrayList<String> spList = userTimeSpentSorter.get(count);

            for (String sp : spList) {
                //instantiate LinkedHashMap to store set of results to be stored into resultList
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("rank", rank);
                result.put("semantic-place", sp);
                result.put("count", count);

                //adding the result into resultList to be returned
                resultList.add(result);
            }
            rank++;
        }

        //inserting of results into topKNextPlacesResult
        topKNextPlacesResult.put("status", "success");
        topKNextPlacesResult.put("total-users", totalUsers);
        topKNextPlacesResult.put("total-next-place-users", totalNextPlaceUsersCount);
        topKNextPlacesResult.put("results", resultList);

        return topKNextPlacesResult;

    }
}
