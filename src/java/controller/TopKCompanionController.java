package controller;

import dao.DemographicsDAO;
import entity.Demographics;
import entity.Location;
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
import utility.Validation;
import utility.TopKUtility;

/** 
 * Top K Companion Controller deals returning the top k companion of a specified mac address.
 * @author Eugene
 */
public class TopKCompanionController {

    /**
     * Gets TopKCompanion data, based on given specified user mac-address,
     * endDateTime and k.
     *
     * @param macAddress to be requested
     * @param endDateTime Date and Time to be requested
     * @param k rank to be requested
     * @return LinkedHashMap of TopKCompanion data with given mac-address,
     * endDateTime and k.
     */
    public LinkedHashMap getTopKCompanion(String macAddress, String endDateTime, String k, String token, boolean fromJson) {
        //instantiate LinkedHashMap to store final result to return
        LinkedHashMap<String, Object> topKCompanionResult = new LinkedHashMap<String, Object>();

        //instantiate ArrayList to store error messages
        ArrayList<String> errorMessageList = new ArrayList<String>();

        //instantiate ArrayList to store results of final companion list to be stored in topKCompanionResult
        ArrayList<LinkedHashMap> resultList = new ArrayList<LinkedHashMap>();

        //instantiate LinkedHashMap to store computed time together to be stored in topKCompanionResult
        HashMap<String, Integer> timeTogetherCounter = new HashMap<String, Integer>();
        //instantiate treeMap to store computed time together from LinkedHashMap in specified ranking
        TreeMap<Integer, ArrayList<String>> timeTogetherSorter = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

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

        //check for empty datetime, or null datetime
        if (endDateTime == null) {

            //Check for missing date
            errorMessageList.add(ErrorMessage.getMsg("missingDate"));
        } else if (endDateTime.length() == 0) {

            //Check for blank date
            errorMessageList.add(ErrorMessage.getMsg("blankDate"));

        } else {
            //check for valid date
            if (!DateValidation.validateDateFormat(endDateTime)) {
                errorMessageList.add(ErrorMessage.getMsg("invalidDate"));
            }
        }

        //check for empty email
        if (macAddress == null) {
            errorMessageList.add(ErrorMessage.getMsg("missingMacAddress"));
            //check for blank email
        } else if (macAddress.length() == 0) {
            errorMessageList.add(ErrorMessage.getMsg("blankMacAddress"));
        } else {
            //check for email validity
            if (!Validation.validateMacAddress(macAddress)) {
                errorMessageList.add(ErrorMessage.getMsg("invalidMacAddress"));
            }
        }
        //initialize kInt to the default value of 3 for "k"
        int kInt = 3;
        //check if k is empty or null
        if (k != null && k.length() != 0) {
            try {
                //check if k is a valid number
                kInt = Integer.parseInt(k);
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
            topKCompanionResult.put("status", "error");
            topKCompanionResult.put("messages", errorMessageList);
            return topKCompanionResult;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        //instantiate secondDateTime to store end query window time
        String startDateTime = null;

        //remove "T" from dateTime input 
        endDateTime = endDateTime.replace("T", " ");

        //Let's store the second date time format into secondDateTime variable
        startDateTime = DateTimeUtility.getTimeBefore(endDateTime, 15);

        /* 
         * IMPT!!! this method gets all the location updates from a specific startDateTime and endDateTime
         * For each mac-address, we will retrieve all of its location updates and we will do pairing
         *      e.g mac-address has (2014-03-23 11:46:01, 2014-03-23 11:48:00, 2014-03-23 11:58:00, 2014-03-23 12:00:00), we will do pairing as such
         *      [] => inclusive, () => exclusive
         *      ArrayList<Location> pair1: [2014-03-23 11:46:01, 2014-03-23 11:48:00)   => time together = 1 min 59s (just take difference)
         *      ArrayList<Location> pair2: [2014-03-23 11:48:00, 2014-03-23 11:57:00)
         *       => take note, if difference between 2 updates exceed 9 minutes, we will split into 2 pairs
         *      ArrayList<Location> pair3: [2014-03-23 11:57:00, 2014-03-23 11:58:00) (this is the pair in which the user is outside SIS and during computation of time together it will be skipped)
         *      ArrayList<Location> pair4: [2014-03-23 11:58:00, 2014-03-23 12:00:00]   => time together = 2 min 1s (must plus 1 second because the endpoint is inclusive
         */
        HashMap<String, ArrayList<ArrayList<Location>>> locationMap = TopKUtility.getAllLocationPairsMacAddress(startDateTime, endDateTime);
        
        // if the specified macAddress has no key, it means that within the specified window, there is no update for him and we will just return empty resultlist
        if (!locationMap.containsKey(macAddress)) {
            topKCompanionResult.put("status", "success");
            topKCompanionResult.put("results", resultList);
            return topKCompanionResult;
        }

        // This point onwards assumes that specified mac address has at least one or more location updates
        // The below ArrayList<ArrayList<Location>> is all the location updates (in pairs)
        ArrayList<ArrayList<Location>> userLocationUpdates = locationMap.get(macAddress);

        // Getting the keySet (mac-addresses that has at least one or more updates too) of the locationMap to iterate through the companions' mac address
        Set<String> macAddressList = locationMap.keySet();
        Iterator<String> macAddressIter = macAddressList.iterator();

        // we would want to loop through each mac address that is found in the location map
        while (macAddressIter.hasNext()) {

            // retrieve the mac-address from the locationMap
            String companionMacAddress = macAddressIter.next();

            // if the retrieved mac-address is not a companion's mac-address but the specified mac-address, we just skip it
            if (macAddress.equals(companionMacAddress)) {
                continue;
            }

            // retrieve all the location pairs for each mac address
            ArrayList<ArrayList<Location>> companionLocationUpdates = locationMap.get(companionMacAddress);

            // this method retrieves the total time together map between 2 mac-addresses (based on every pair of their location updates)
            ArrayList<HashMap<String, String>> timeTogetherList = TopKUtility.computeTotalTimeTogether(userLocationUpdates, companionLocationUpdates);

            int timeTogether = 0;
            for (HashMap timeTogetherEntry : timeTogetherList) {
                String timeTogetherString = (String) timeTogetherEntry.get("time-spent");
                timeTogether += Integer.parseInt(timeTogetherString);
                System.out.println(timeTogether);
            }

            // storing the time together in a map in which the mac-address is the key and time together is the value
            timeTogetherCounter.put(companionMacAddress, timeTogether);
        }

        // Below loop converts timeTogetherCounter => timeTogetherSorter
        //to retrieve list of companion macAddress from finalized time together computation in timeTogetherCounter LinkedHashMap
        Set<String> companionMacAddressList = timeTogetherCounter.keySet();
        Iterator<String> iter = companionMacAddressList.iterator();

        while (iter.hasNext()) {

            // retrieve the companion mac-address
            String companionMacAddress = iter.next();

            //retrieve finalized time together for specific macAddress
            Integer time = timeTogetherCounter.get(companionMacAddress);

            //to retrieve list of macAddress from timeTogetherSorter that share same time together if any
            ArrayList<String> macList = timeTogetherSorter.get(time);

            if (macList == null) {
                //means the specific time together does not have any macAddress and we would want to create a new ArrayList to store macAddress under it
                macList = new ArrayList<String>();
            }
            //we add this companionMacAddress to existing list or new list if time together does not exist in timeTogetherSorter
            macList.add(companionMacAddress);

            //we put the updated list into timeTogetherSorter
            timeTogetherSorter.put(time, macList);
        }

        //to include only requested rank into topKCompanionResult to be returned
        int rank = 1;

        //retrieve set of time together from timeTogetherSorter
        Set<Integer> timeList = timeTogetherSorter.keySet();

        // looping through the timelist so as to select just the k number as specified by the user
        Iterator<Integer> iter2 = timeList.iterator();
        while (iter2.hasNext() && rank <= kInt) {
            Integer time = iter2.next();
            if (time == 0) {
                break;
            }
            //retrieve list of macAddress from timeTogetherSorter for specified time together
            ArrayList<String> macList = timeTogetherSorter.get(time);
            for (String mac : macList) {
                //instantiate new LinkedHashMap to store results to appointed rank
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

                //put rank into LinkedHashMap result
                result.put("rank", rank);
                DemographicsDAO demoDao = new DemographicsDAO();

                //retrieve demographics object from demographics dao if particular macAddress exist
                Demographics demo = demoDao.retrieveByMacAddress(mac);
                String email2 = "";
                if (demo != null) {
                    //if demo is not null, it means particular demographics under this macAddress is in db
                    email2 = demo.getEmail();
                }
                //put email of particular demographics into result if exist or empty string if does not exist
                result.put("companion", email2);

                //put macAddress into result
                result.put("mac-address", mac);

                //put time together into result
                result.put("time-together", time);

                //add LinkedHashMap result into resultList arrayList to be returned
                resultList.add(result);
            }
            rank++;
        }

        System.out.println(resultList);
        topKCompanionResult.put("status", "success");
        topKCompanionResult.put("results", resultList);

        return topKCompanionResult;
    }
}
