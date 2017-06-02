package controller;

import dao.LocationLookupDAO;
import entity.Location;
import entity.LocationLookup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;
import utility.TopKUtility;

/**
 * GroupTopKNextPlacesController class contains methods that will be used to
 * retrieve the Group Top K Next Places of the groups identified in the previous
 * 15 minute window.
 *
 * @author Jiacheng/Eugene
 */
public class GroupTopKNextPlacesController {

    /**
     * Retrieves a LinkedHashMap of the Breakdown of Demographics in SIS within
     * the time span indicated
     *
     * @return LinkedHashMap Returns a LinkedHashMap of Group Top K Next Places
     * results which consist of status, total-groups, total-next-places-groups,
     * results
     * @param date Specifies the time span window's end date time to retrieve
     * the Group Top K Next Places results
     * @param token Specifies the token, to be validated
     * @param origin The last semantic place that was spent together by the
     * group
     * @param k The number of rank of Group Top K Next Places to be displayed.
     * @param fromJson This will be set to true if it is called by the Json Web
     * Services. If it is true, then token validation will ensure it comes from
     * the admin.
     */
    public LinkedHashMap getTopKGroupNextPlaces(String origin, String date, String k, String token, boolean fromJson) {

        // Initialize the AGD controller to retrieve results from AGD later
        AutomaticGroupDetectionController agdController = new AutomaticGroupDetectionController();

        // Instantiate a LinkedHashMap to store the results to be returned
        LinkedHashMap<String, Object> resultsMap = new LinkedHashMap<String, Object>();

        // Instantiate an arraylist to store all the error messages to be returned.
        ArrayList<String> messages = new ArrayList<String>();

        // Set k number as the default value 3
        int kNumber = 0;

        // Set endDateTime (15 minutes later)
        String endDateTime = null;

        // Set a dateWithT as the date that the user queried but with T instead of a white space
        String dateWithT = date;

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
        //Initialize LocationLookupDAO
        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();

        //check for empty origin
        if (origin == null) {
            messages.add(ErrorMessage.getMsg("missingOrigin"));
            //check for blank email
        } else if (origin.length() == 0) {
            messages.add(ErrorMessage.getMsg("blankOrigin"));
        } else {
            //check for valid origin place
            LocationLookup semanticPlace = locationLookupDAO.retrieve(origin);
            if (semanticPlace == null) {
                messages.add(ErrorMessage.getMsg("invalidOrigin"));
            }
        }

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
        if (date == null) {

            //Check for missing date
            messages.add(ErrorMessage.getMsg("missingDate"));
        } else if (date.length() == 0) {

            //Check for blank date
            messages.add(ErrorMessage.getMsg("blankDate"));

        } // Check for valid date
        else {

            // At this stage, the format is assumed to have a T, and it is not empty.
            // Validate endDateTime to have the correct format
            if (!DateValidation.validateDateFormat(date)) {
                messages.add(ErrorMessage.getMsg("invalidDate"));
            } else {

                // endDateTime is of correct format, replace T with white space.
                date = date.replace('T', ' ');

                // Retrieve a startDateTime of 15 minutes before.
                endDateTime = DateTimeUtility.getTimeAfter(date, 15);
            }
        }

        // If messages size is bigger than 0, means there is an error, and we should display error messages
        if (messages.size() > 0) {

            // Sort errors according to alphabetical order
            Collections.sort(messages);

            // Put status as "error"
            resultsMap.put("status", "error");

            // Put messages as messages
            resultsMap.put("messages", messages);

            // Return immediately, as there is no longer a need to process further
            return resultsMap;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        // Retrieve group detection results from the previous 15 mins window
        LinkedHashMap<String, Object> agdResults = agdController.getAutomaticGroupDetection(dateWithT, token, true, fromJson);

        // Initalize an arraylist of linkedhashmap to store the top k next places results
        ArrayList<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

        if (agdResults.get("total-groups") == null) {
            resultsMap.put("status", "success");
            resultsMap.put("total-groups", 0);
            resultsMap.put("total-next-place-groups", 0);
            resultsMap.put("results", results);
            return resultsMap;
        }
        // Retrieve the total number of groups retrieved from AGD
        int totalGroups = (Integer) agdResults.get("total-groups");

        // Initialize the total number of groups which has eligible next place in the next 15 min window
        int totalNextPlaceGroups = 0;

        // Search for groups only if there are groups identified from the previous 15 mins window
        if (!((ArrayList<LinkedHashMap<String, Object>>) agdResults.get("groups")).isEmpty()) {
            // Retrieve the groupList from the AGD results
            ArrayList<LinkedHashMap<String, Object>> groupList = (ArrayList<LinkedHashMap<String, Object>>) agdResults.get("groups");

            // Initialize a HashMap to store the next place semantic place and the number of groups that went there during the next 15 mins
            /*
             [       Key       ] : [ Value ]
             [ semantic_place1 ] : [  10   ]
             [ semantic_place2 ] : [  9    ]
             */
            HashMap<String, Integer> nextSPMap = new HashMap<String, Integer>();
            for (LinkedHashMap group : groupList) {

                // Instantiate the memberList and locationList of the group
                ArrayList<LinkedHashMap> memberList = (ArrayList<LinkedHashMap>) group.get("members");
                ArrayList<LinkedHashMap> locationList = (ArrayList<LinkedHashMap>) group.get("locations");

                // Get last location of the group
                String lastSemanticPlace = (String) group.get("lastSemanticPlace");

                // Only search for next places if the last location of the group matches that of the user's query
                if (lastSemanticPlace.equals(origin)) {

                    // Instantiate a map to store the location pairs of each member in the group in the next 15 mins window
                    /*
                     [ Key (String) ] : [ Value (ArrayList<ArrayList<Location>>) ]
                     [ macAddress 1 ] : [         ArrayList<LocationPairs>       ]
                     [ macAddress 2 ] : [         ArrayList<LocationPairs>       ]
                     [ macAddress 3 ] : [         ArrayList<LocationPairs>       ]
                     */
                    LinkedHashMap<String, ArrayList<ArrayList<Location>>> groupNextWindowLocMap = new LinkedHashMap<String, ArrayList<ArrayList<Location>>>();

                    for (LinkedHashMap memberMap : memberList) {
                        String memberMacAddress = (String) memberMap.get("mac-address");

                        // Get all location update pairs within timeframe of the mac-address
                        ArrayList<ArrayList<Location>> memberLocPairs = TopKUtility.getAllLocPairsOfMacAddress(memberMacAddress, date, endDateTime);

                        // Put all the location pairs into the LinkedHashMap by their mac-address key respectively
                        groupNextWindowLocMap.put(memberMacAddress, memberLocPairs);
                    }

                    // Instantiate an iterator to iterate through the mac-address keys of the gorupNextWindowLocMap
                    Set<String> groupMacAddressSet = groupNextWindowLocMap.keySet();
                    Iterator<String> iter = groupMacAddressSet.iterator();

                    // Instantiate an arraylist to store the common location arrayList between the first member and other members
                    ArrayList<ArrayList<HashMap<String, Object>>> groupCommonLocList = new ArrayList<ArrayList<HashMap<String, Object>>>();

                    // Get the first member's mac-address
                    String firstMemberMacAdd = iter.next();

                    // Get the first member's location pairs for the next 15 mins window
                    ArrayList<ArrayList<Location>> firstMemberLocPairs = groupNextWindowLocMap.get(firstMemberMacAdd);

                    // Compute time together of the first member with each of the other members
                    while (iter.hasNext()) {

                        // Get the next member's mac-address
                        String nextMemberMacAdd = iter.next();

                        // Get the next member's location pairs for the next 15 mins window
                        ArrayList<ArrayList<Location>> nextMemberLocPairs = groupNextWindowLocMap.get(nextMemberMacAdd);

                        // Get an arrayList of common time and location spent together between the first member and the next member for the next 15 mins window
                       /*
                         [
                         {
                         ( Key            ) : ( Value               )
                         ( semantic-place ) : ( SMUSISLOBBYL3       )
                         ( startDateTime  ) : ( 2014-03-24 13:00:00 )
                         ( endDateTime    ) : ( 2014-03-24 13:05:00 )
                         ( time-spent     ) : ( 300                 )
                         }
                         ]
                         */
                        ArrayList<HashMap<String, Object>> pairTogetherList = TopKUtility.computeTimeTogetherBySemanticPlace(firstMemberLocPairs, nextMemberLocPairs);

                        // Iterate the time spent together and remove if time spent < 5 mins (300 sec)
                        for (int i = 0; i < pairTogetherList.size(); i++) {
                            HashMap<String, Object> pairTogether = pairTogetherList.get(i);
                            int timeSpent = (Integer) pairTogether.get("time-spent");
                            if (timeSpent < 300) {
                                pairTogetherList.remove(i);
                            }
                        }
                        System.out.println("***** pairTogetherList: " + pairTogetherList + "*****");

                        // If there are any time spent together which are of 5 mins and above then we will add into the group time spent together list
                        if (pairTogetherList != null && !pairTogetherList.isEmpty()) {
                            groupCommonLocList.add(pairTogetherList);
                        }
                    }

                    // If the first member has a common time spent together with each of the other members then would we continue to search for this group's next places
                    if (groupCommonLocList.size() == memberList.size() - 1) {

                        // Get the group's last common semantic place which is of at least 5 mins
                        String groupNextPlace = getGroupLastNextSP(groupCommonLocList);

                        if (groupNextPlace != null) {

                            // Increase the total next place group count
                            totalNextPlaceGroups++;

                            // Check if the nextSPMap has an exisiting record of the semantic place
                            // If the semantic place is a unique or new semantic place in nextSPMap, put count as 1
                            if (nextSPMap.get(groupNextPlace) == null) {
                                nextSPMap.put(groupNextPlace, 1);
                            } else {

                                // If the nextSPMap has an existing record of the semantic place, get the exisiting count and add 1 and put back into nextSPMap
                                int groupNextPlaceCount = nextSPMap.get(groupNextPlace);
                                nextSPMap.put(groupNextPlace, ++groupNextPlaceCount);
                            }
                        }
                    }
                }
            }

            // Instantiate a new TreeMap to sort the nextSPMap
            TreeMap<Integer, ArrayList<String>> tempMap = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

            // Instantiate an iterator to iterate through the keys of the nextSPMap
            Set<String> nextSPKeySet = nextSPMap.keySet();
            Iterator<String> iter = nextSPKeySet.iterator();
            while (iter.hasNext()) {
                String semanticPlace = iter.next();

                // Get the number of groups which has that semantic place as their last next place
                int semanticPlaceCount = nextSPMap.get(semanticPlace);

                // If the number of groups is new or unique, create a new arrayList and store the semantic place
                if (tempMap.get(semanticPlaceCount) == null) {
                    ArrayList<String> semanticPlaceList = new ArrayList<String>();
                    semanticPlaceList.add(semanticPlace);
                    tempMap.put(semanticPlaceCount, semanticPlaceList);

                    // If there is an existing same number of groups, get the arrayList of semantic place and add the semantic place into the arrayList of semantic place
                } else {
                    ArrayList<String> semanticPlaceList = tempMap.get(semanticPlaceCount);
                    semanticPlaceList.add(semanticPlace);
                    tempMap.put(semanticPlaceCount, semanticPlaceList);
                }

            }

            // Instantiate the rank
            int rank = 1;

            // Instantiate a set of mapEntry of the tempMap
            Set<Map.Entry<Integer, ArrayList<String>>> tempSet = tempMap.entrySet();
            for (Map.Entry<Integer, ArrayList<String>> temp : tempSet) {

                // Get the count (number of groups)
                int num_groups = temp.getKey();

                // Get the arrayList of semantic place which has the count (number of groups)
                ArrayList<String> semanticPlaceList = temp.getValue();

                // For each of the semantic place which has the same count
                for (String semanticPlace : semanticPlaceList) {

                    // Instantiate a linkedHashMap to store the the semantic place's details (rank, semantic-place and num-groups)
                    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                    result.put("rank", rank);
                    result.put("semantic-place", semanticPlace);
                    result.put("num-groups", num_groups);

                    // Add the linkedHashMap into the arrayList of next place results
                    results.add(result);
                }

                // At this point, the next num_groups (count) will be of a lower rank
                rank++;

                // If the rank exceeds the number that the user queries for, break out of this for loop
                if (rank > kNumber) {
                    break;
                }
            }

        }

        // Put the respective keys and values into the resultsMap to be returned
        resultsMap.put("status", "success");
        resultsMap.put("total-groups", totalGroups);
        resultsMap.put("total-next-place-groups", totalNextPlaceGroups);
        resultsMap.put("results", results);
        return resultsMap;
    }

    /**
     * Retrieves a String name of the semantic place last visited by the group
     * of at least 5 minutes.
     *
     * @return String The last next semantic place spent together by this group.
     * @param groupCommonLocList An ArrayList of ArrayList of HashMap that
     * consist of the time spent together details of a group.
     */
    public String getGroupLastNextSP(ArrayList<ArrayList<HashMap<String, Object>>> groupCommonLocList) {
        // Get the arrayList of time spent together of the first member and the second member and remove it from the main arrayList
        ArrayList<HashMap<String, Object>> firstPairCommonLocList = groupCommonLocList.remove(0);

        // For each of the rest of the arrayList of time spent together between the first member and the other members
        for (ArrayList<HashMap<String, Object>> otherPairCommonLocList : groupCommonLocList) {

            // Iterate through the time spent together of the first member and second member from the last time spent together
            for (int i = firstPairCommonLocList.size() - 1; i >= 0; i--) {

                // Get the time spent together
                HashMap<String, Object> firstPairCommonLoc = firstPairCommonLocList.get(i);

                // Get the semantic place of the time spent together
                String firstPairCommonLocSP = (String) firstPairCommonLoc.get("semantic-place");

                // Instantiate a boolean checker to determine if the 1st & 2nd pair and 1st & next pair has any common time spent for this particular semantic place
                boolean pairsTogether = false;

                // Iterate through the time spent together of the first and next member from the last time spent together
                for (int j = otherPairCommonLocList.size() - 1; j >= 0; j--) {

                    // Get the time spent together
                    HashMap<String, Object> nextPairCommonLoc = otherPairCommonLocList.get(i);

                    // Get the semantic place of the time spent together
                    String otherPairCommonLocSP = (String) nextPairCommonLoc.get("semantic-place");

                    // If this semantic place matches that of the first and second member's time spent together
                    if (firstPairCommonLocSP.equals(otherPairCommonLocSP)) {

                        // Get the startDateTime and endDateTime of the time spent together of 1st & 2nd pair and 1st & next pair
                        String firstPairSD = (String) firstPairCommonLoc.get("startDateTime");
                        String firstPairED = (String) firstPairCommonLoc.get("endDateTime");
                        String nextPairSD = (String) nextPairCommonLoc.get("startDateTime");
                        String nextPairED = (String) nextPairCommonLoc.get("endDateTime");

                        // Compare the startDateTime and endDateTime between these 2 pairs
                        int compareSD = DateTimeUtility.compareDates(firstPairSD, nextPairSD);
                        int compareED = DateTimeUtility.compareDates(firstPairED, nextPairED);

                        // Initialize limiting window of these 2 pairs
                        String startDate = "";
                        String endDate = "";

                        // If the startDateTime of 1st & next pair is later than that of the 1st & 2nd pair, take the next pair's startDateTime as the stateDate of the limiting window
                        if (compareSD == -1) {
                            startDate = nextPairED;

                            // If the startDateTime of the 1st & 2nd pair is later than that of the 1st & next pair, take the 1st pair's startDateTime as the startDate of the limiting window
                        } else if (compareSD == 1) {
                            startDate = firstPairED;

                            // If they are equal, just take the 1st & 2nd pair's startDateTime as the startDate
                        } else {
                            startDate = firstPairED;
                        }

                        // If the endDateTime of 1st & 2nd pair is earlier than that of the 1st & next pair, take the 1st pair's endDateTime as the endDate of the limiting window
                        if (compareED == -1) {
                            endDate = firstPairED;

                            // If the endDateTime of 1st & next pair is earlier than that of the 1st & 2nd pair, take the next pair's endDateTime as the endDate of the limiting window
                        } else if (compareED == 1) {
                            endDate = nextPairED;

                            // If they are equal, just take the endDateTime of the 1st pair
                        } else {
                            endDate = firstPairED;
                        }

                        // Calculate the time spent together between the 1st & 2nd pair and 1st & next pair
                        int twoPairTimeTogether = DateTimeUtility.getTimeDiffInSeconds(startDate, endDate);

                        // If time spent together is more than 5 mins (300 sec), set checker to true as 1st & next pair has at least 5 mins spent together with 1st & 2nd pair
                        if (twoPairTimeTogether > 300) {
                            pairsTogether = true;
                        }
                    }
                }
                // If this 1st & next pair does not have a common time spent together, remove this time spent together from 1st & 2nd pair so that we don't have to check it with other groups
                if (!pairsTogether) {
                    firstPairCommonLocList.remove(i);
                }
            }
        }

        if (!firstPairCommonLocList.isEmpty()) {
            HashMap<String, Object> lastSemanticPlaceMap = firstPairCommonLocList.get(firstPairCommonLocList.size() - 1);
            String lastSemanticPlace = (String) lastSemanticPlaceMap.get("semantic-place");
            return lastSemanticPlace;
        }

        return null;
    }
}
