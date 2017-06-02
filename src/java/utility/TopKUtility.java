package utility;

import dao.LocationDAO;
import dao.LocationLookupDAO;
import entity.Location;
import entity.LocationLookup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * TopKUtility provides supplementary methods for Top K popular places,
 * companions, next places and group aware reports to compute time-together by
 * location ids, semantic places, etc
 *
 * @author Eugene , Jing Xiang
 */
public class TopKUtility {

    /**
     * <br />This method returns all the location updates from a specific
     * startDateTime and endDateTime For each mac-address, we will retrieve all
     * of its location updates and we will do pairing e.g mac-address has
     * (2014-03-23 11:46:01, 2014-03-23 11:48:00, 2014-03-23 11:58:00,
     * 2014-03-23 12:00:00), we will do pairing as such [] => inclusive, () =>
     * exclusive ArrayList<Location> pair1: [2014-03-23 11:46:01, 2014-03-23
     * 11:48:00) => time together = 1 min 59s (just take difference)
     * ArrayList<Location> pair2: [2014-03-23 11:48:00, 2014-03-23 11:57:00) =>
     * take note, if difference between 2 updates exceed 9 minutes, we will
     * split into 2 pairs ArrayList<Location> pair3: [2014-03-23 11:57:00,
     * 2014-03-23 11:58:00) (this is the pair in which the user is outside SIS
     * and during computation of time together it will be skipped)
     * ArrayList<Location> pair4: [2014-03-23 11:58:00, 2014-03-23 12:00:00] =>
     * time together = 2 min 1s (must plus 1 second because the endpoint is
     * inclusive
     *
     * @param startDateTime the start date time of specified query window
     * @param endDateTime the end date time of specified query window
     * @return a LinkedHashMap of LocationPairs for a mac address, empty
     * LinkedHashMap if no records found
     */
    public static LinkedHashMap getAllLocationPairsMacAddress(String startDateTime, String endDateTime) {

        // create an overview map to store all location update pairs found within query window
        LinkedHashMap<String, ArrayList<ArrayList<Location>>> locationPairMap = new LinkedHashMap<String, ArrayList<ArrayList<Location>>>();

        // instantiating the locationDAO
        LocationDAO locationDAO = new LocationDAO();

        // retrieve unique mac-addresses (1 of each mac-address) that have 1 or more location update within the query window
        ArrayList<String> macAddressList = locationDAO.getWindowUniqueMacAddresses(startDateTime, endDateTime);

        // if there is no macAddress found at all, there is no need to continue computing the time together
        if (macAddressList.isEmpty()) {
            return locationPairMap;
        }

        // looping through all the unique mac-addresses to find and store all location updates into pairs for each
        for (String macAddress : macAddressList) {

            // the method retrieves all updates within the query window
            ArrayList<Location> locationList = locationDAO.getUserUpdates(macAddress, startDateTime, endDateTime);

            // a userArrayList stores all the pairs of location updates for a specific mac-address
            ArrayList<ArrayList<Location>> userArrayList = new ArrayList<ArrayList<Location>>();

            // store it in the overview map
            locationPairMap.put(macAddress, userArrayList);

            // Looping through all the location updates found to do pairing
            for (int n = 0; n < locationList.size(); n++) {

                // retrieve the first location update of a pair
                Location location1 = locationList.get(n);

                // retrieve the timestamp for the first location update of a pair
                String timestamp1 = location1.getTimestamp();

                // retrieve the location id for the first location update of a pair
                int locationid1 = location1.getLocationId();

                // instantiate the first pair which is between the 15 minutes before and user's first update
                ArrayList<Location> firstPair = new ArrayList<Location>();

                // instantite the first location (outside SIS), which is  before the user is there for his first update
                Location startLocation = new Location(startDateTime, macAddress, 0);
                firstPair.add(startLocation);
                firstPair.add(location1);

                // if the first location update of a pair is at the bottom of the list, we will not want to continue
                if (n < locationList.size() - 1) {

                    // retrieve the second location update of a pair
                    Location location2 = locationList.get(n + 1);

                    // retrieve the timestamp for the first location update of a pair
                    String timestamp2 = location2.getTimestamp();

                    // retrieve the location id for the first location update of a pair
                    String locationid2 = location2.getTimestamp();

                    // splitting into two pairs if the difference is greater than 9 minutes
                    if (DateTimeUtility.getTimeDiffInSeconds(timestamp1, timestamp2) > 60 * 9) {

                        // instantiate the extra pair
                        ArrayList<Location> additionalPair = new ArrayList<Location>();

                        // instantiate thet outside SIS location with locationId = 0 so that during computation of time together, we will eliminate it
                        Location outsideSIS = new Location(DateTimeUtility.getTimeAfter(timestamp1, 9), macAddress, 0);
                        additionalPair.add(location1);
                        additionalPair.add(outsideSIS);

                        // add the addition pair into the user's main arrayList
                        userArrayList.add(additionalPair);

                        // rotating the order of location1 to be outsideSIS
                        location1 = outsideSIS;
                    }

                    // instantiate a pair to store location updates
                    ArrayList<Location> pair = new ArrayList<Location>();
                    pair.add(location1);
                    pair.add(location2);

                    // add the pair into the user's main arrayList
                    userArrayList.add(pair);

                } else {
                    // this else means that we are at the bottom of the list
                    // 2 cases here: either the last update + 9 is still within the endDateTime or it exceeds the endDateTime

                    // instantiating the end pair to store location updates
                    ArrayList<Location> endPair = new ArrayList<Location>();

                    // initialize the endTimestamp to be 9 minutes after the last update before we do the filtering
                    String endTimestamp = DateTimeUtility.getTimeAfter(timestamp1, 9);

                    // if the endDateTime is before the endTimestamp (9 minutes after last update), we will want to restrict the endTimestamp
                    if (DateTimeUtility.compareDates(endDateTime, endTimestamp) == -1) {

                        // since the endDateTime is inclusive, we would want to add a second to the endDateTime before assigning to endTimestamp
                        //refactored by Kenneth on 26th October 2014 to leave out the +1 sec.
                        endTimestamp = endDateTime;
                    }

                    // instantiating the endLocation which signifies that the user is "outside SIS"
                    Location endLocation = new Location(endTimestamp, macAddress, 0);
                    endPair.add(location1);
                    endPair.add(endLocation);
                    userArrayList.add(endPair);
                }
            }
        }
        return locationPairMap;
    }

    /**
     * <br />Compute the total time together for update pairs
     * (ArrayList<ArrayList<Location>> of 2 mac addresses
     *
     * @param allUpdate1 specified update pair of first mac-address
     * @param allUpdate2 specified update pair of second mac-address
     * @return ArrayList<HashMap<String, String>> which contains records of
     * locationid, time-spent, startdatetime and enddatetime that they are
     * together
     */
    public static ArrayList<HashMap<String, String>> computeTotalTimeTogether(ArrayList<ArrayList<Location>> allUpdate1, ArrayList<ArrayList<Location>> allUpdate2) {
        // Instantiate an ArrayList<HashMap<String, String>> to store all records of locationid, time-spent, startdatetime and enddatetime that they are together
        ArrayList<HashMap<String, String>> timeTogetherList = new ArrayList<HashMap<String, String>>();

        // though it is unlikely for each all update list to be empty, we just return 0 to make sure
        if (allUpdate1.isEmpty() || allUpdate2.isEmpty()) {
            return timeTogetherList;
        }
        // initiatizing n (which is the index of allUpdate1) 
        int n = 0;

        // initiatizing m (which is the index of allUpdate2)
        int m = 0;

        // we will continuing looping the all the pairs of location updates until 1 has reach its end point
        while (n < allUpdate1.size() && m < allUpdate2.size()) {
            ArrayList<Location> updatePair1 = allUpdate1.get(n);
            ArrayList<Location> updatePair2 = allUpdate2.get(m);

            // retrieve all the location updates for both update pairs
            Location update1Location1 = updatePair1.get(0);
            Location update1Location2 = updatePair1.get(1);
            Location update2Location1 = updatePair2.get(0);
            Location update2Location2 = updatePair2.get(1);

            // retrieve all the timestamps for all location updates
            String update1Timestamp1 = update1Location1.getTimestamp();
            String update1Timestamp2 = update1Location2.getTimestamp();
            String update2Timestamp1 = update2Location1.getTimestamp();
            String update2Timestamp2 = update2Location2.getTimestamp();

            // retrieve the first locationId of both update pairs
            int update1LocationId1 = update1Location1.getLocationId();
            int update2LocationId1 = update2Location1.getLocationId();

            // instantianting the startDateTime and endDateTime to store the interception startDateTime and endDateTime
            String startDateTime = null;
            String endDateTime = null;

            // if the start of update pair 1 is before start of update pair 2, we just set the startDateTime as update pair2's start, vice-versa
            if (DateTimeUtility.compareDates(update1Timestamp1, update2Timestamp1) == -1) {
                startDateTime = update2Timestamp1;
            } else {
                startDateTime = update1Timestamp1;
            }

            // if the end of update pair 1 is after end of update pair 2, we just set the endDateTime as update pair2's end, vice-versa
            if (DateTimeUtility.compareDates(update1Timestamp2, update2Timestamp2) == 1) {
                endDateTime = update2Timestamp2;
                m++;

                // if both end points are the same, we would want to move the indexes of both together
            } else if (DateTimeUtility.compareDates(update1Timestamp2, update2Timestamp2) == 0) {
                endDateTime = update1Timestamp2;
                n++;
                m++;
            } else {
                endDateTime = update1Timestamp2;
                n++;
            }

            // only if the first locationID of both update pairs match and not == 0 (outside SIS) then we compute the time together
            if (update1LocationId1 == update2LocationId1 && update1LocationId1 != 0) {
                // return the intersection time together
                int diff = DateTimeUtility.getTimeDiffInSeconds(startDateTime, endDateTime);
                // instantiate one entry of togetherness 
                HashMap<String, String> togetherEntry = new HashMap<String, String>();
                togetherEntry.put("location", "" + update1LocationId1);
                togetherEntry.put("time-spent", "" + diff);
                togetherEntry.put("startDateTime", startDateTime);
                togetherEntry.put("endDateTime", endDateTime);
                timeTogetherList.add(togetherEntry);
            }
        }
        return timeTogetherList;
    }

    /**
     * <br />Retrieve all Location pairs of one specified macAddress given a
     * startDateTime and endDateTime
     *
     * @param macAddress specified macAddress of user - used to find his
     * location pairs
     * @param startDateTime start date time of query window
     * @param endDateTime end date time of query window
     *
     * @return an ArrayList<ArrayList<Location>> of location pairs for a
     * specified mac-address
     */
    public static ArrayList<ArrayList<Location>> getAllLocPairsOfMacAddress(String macAddress, String startDateTime, String endDateTime) {

        // instantiating the locationDAO
        LocationDAO locationDAO = new LocationDAO();

        // the method retrieves all updates within the query window
        ArrayList<Location> locationList = locationDAO.getUserUpdates(macAddress, startDateTime, endDateTime);

        // a userArrayList stores all the pairs of location updates for a specific mac-address
        ArrayList<ArrayList<Location>> userArrayList = new ArrayList<ArrayList<Location>>();

        // Looping through all the location updates found to do pairing
        for (int n = 0; n < locationList.size(); n++) {

            // retrieve the first location update of a pair
            Location location1 = locationList.get(n);

            // retrieve the timestamp for the first location update of a pair
            String timestamp1 = location1.getTimestamp();

            // retrieve the location id for the first location update of a pair
            int locationid1 = location1.getLocationId();

            // instantiate the first pair which is between the 15 minutes before and user's first update
            ArrayList<Location> firstPair = new ArrayList<Location>();

            // instantite the first location (outside SIS), which is  before the user is there for his first update
            Location startLocation = new Location(startDateTime, macAddress, 0);
            firstPair.add(startLocation);
            firstPair.add(location1);

            // if the first location update of a pair is at the bottom of the list, we will not want to continue
            if (n < locationList.size() - 1) {

                // retrieve the second location update of a pair
                Location location2 = locationList.get(n + 1);

                // retrieve the timestamp for the first location update of a pair
                String timestamp2 = location2.getTimestamp();

                // retrieve the location id for the first location update of a pair
                String locationid2 = location2.getTimestamp();

                // splitting into two pairs if the difference is greater than 9 minutes
                if (DateTimeUtility.getTimeDiffInSeconds(timestamp1, timestamp2) > 60 * 9) {

                    // instantiate the extra pair
                    ArrayList<Location> additionalPair = new ArrayList<Location>();

                    // instantiate thet outside SIS location with locationId = 0 so that during computation of time together, we will eliminate it
                    Location outsideSIS = new Location(DateTimeUtility.getTimeAfter(timestamp1, 9), macAddress, 0);
                    additionalPair.add(location1);
                    additionalPair.add(outsideSIS);

                    // add the addition pair into the user's main arrayList
                    userArrayList.add(additionalPair);

                    // rotating the order of location1 to be outsideSIS
                    location1 = outsideSIS;
                }

                // instantiate a pair to store location updates
                ArrayList<Location> pair = new ArrayList<Location>();
                pair.add(location1);
                pair.add(location2);

                // add the pair into the user's main arrayList
                userArrayList.add(pair);

            } else {
                    // this else means that we are at the bottom of the list
                // 2 cases here: either the last update + 9 is still within the endDateTime or it exceeds the endDateTime

                // instantiating the end pair to store location updates
                ArrayList<Location> endPair = new ArrayList<Location>();

                // initialize the endTimestamp to be 9 minutes after the last update before we do the filtering
                String endTimestamp = DateTimeUtility.getTimeAfter(timestamp1, 9);

                // if the endDateTime is before the endTimestamp (9 minutes after last update), we will want to restrict the endTimestamp
                if (DateTimeUtility.compareDates(endDateTime, endTimestamp) == -1) {

                    // since the endDateTime is inclusive, we would want to add a second to the endDateTime before assigning to endTimestamp
                    endTimestamp = endDateTime;
                }

                // instantiating the endLocation which signifies that the user is "outside SIS"
                Location endLocation = new Location(endTimestamp, macAddress, 0);
                endPair.add(location1);
                endPair.add(endLocation);
                userArrayList.add(endPair);
            }
        }
        return userArrayList;
    }

    /**
     * <br />Used in Top K Next Places to retrieve last visited semantic place based
     * on specified update (ArrayList<ArrayList<Location>>) in the next 15
     * minutes
     *
     * @param allUpdate specified update which contains all location pairs in
     * the next 15 minutes
     *
     * @return the last semantic place visited, null if no records found 
     */
    public static String computeVisitedSemanticPlace(ArrayList<ArrayList<Location>> allUpdate) {

        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();
        // though it is unlikely for each all update list to be empty, we just return 0 to make sure
        if (allUpdate.isEmpty()) {
            return null;
        }

        // initiatizing n (which is the index of allUpdate) 
        int n = allUpdate.size() - 1;

        // instantiate storedSemanticPlace to track of previous location update semantic place
        String storedSemanticPlace = null;
        //instantiate timeAccumulated to keep track of accumulated time spent of same location updates
        int timeAccumulated = 0;

        // we will continuing looping (from the back) the all the pairs of location updates until 1 has reach its end point
        while (n >= 0) {
            ArrayList<Location> updatePair1 = allUpdate.get(n);

            // retrieve all the location updates
            Location update1Location1 = updatePair1.get(0);
            Location update1Location2 = updatePair1.get(1);

            // retrieve all the timestamps for all location updates
            String update1Timestamp1 = update1Location1.getTimestamp();
            String update1Timestamp2 = update1Location2.getTimestamp();
            System.out.println(update1Timestamp1 + " vs " + update1Timestamp2);
            // retrieve the first locationId
            int update1LocationId1 = update1Location1.getLocationId();

            // retrieve the semantic-place of the first locationid
            LocationLookup locationLookup1 = locationLookupDAO.retrieve(update1LocationId1);
            String semanticPlace1 = "outsideSIS";
            if (locationLookup1 != null) {
                semanticPlace1 = locationLookup1.getSemanticPlace();
            }
            //compute time difference between 2 location updates
            int timeDiff = DateTimeUtility.getTimeDiffInSeconds(update1Timestamp1, update1Timestamp2);

            //check if this location update is of similar semantic place as previous update
            if (semanticPlace1.equals(storedSemanticPlace)) {
                //means its the same and we would like to accumulate the time spent of this location update as well as the previous location update
                timeAccumulated += timeDiff;
            } else {
                //means this location update is different from previous update
                timeAccumulated = timeDiff;
                //check if user spent more than 5mins at this location update
                if (timeAccumulated >= 5 * 60) {
                    //means user spent more than 5mins at this location and we return user current location update as the next visited place
                    System.out.println("Next Visited Place:" + semanticPlace1);
                    System.out.println("Time Spent:" + timeAccumulated);
                    return semanticPlace1;
                }
            }

            //check if total accumulated time is more than 5mins if this location update is same as previous location update
            if (timeAccumulated >= 5 * 60) {
                //means it passes 5mins rule and we would want to return as the  next visited place
                System.out.println("Next Visited Place:" + storedSemanticPlace);
                System.out.println("Time Spent:" + timeAccumulated);
                return storedSemanticPlace;
            }
            //assigning current semantic place to storedSemanticPlace before we progress to next location update
            storedSemanticPlace = semanticPlace1;
            n--;
        }
        return null;
    }
    
    /**
     * <br />Used in Group Top K Next Places to together records based on two updates of location pair in the next 15 minutes
     * 
     * @param allUpdate1 the first update of location pair
     * @param allUpdate2 the second update of location pair
     * 
     * @return ArrayList<HashMap<String, Object>> which contains an arraylist of together records in the next 15 minutes
     */
    public static ArrayList<HashMap<String, Object>> computeTimeTogetherBySemanticPlace(ArrayList<ArrayList<Location>> allUpdate1, ArrayList<ArrayList<Location>> allUpdate2) {
        
        // instantiate the nextPlaceList 
        ArrayList<HashMap<String, Object>> nextPlaceList = new ArrayList<HashMap<String, Object>>();

        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();

        // though it is unlikely for each all update list to be empty, we just return 0 to make sure
        if (allUpdate1.isEmpty() || allUpdate2.isEmpty()) {
            return nextPlaceList;
        }

        // initiatizing n (which is the index of allUpdate1) 
        int n = 0;

        // initiatizing m (which is the index of allUpdate2)
        int m = 0;

        // we will continuing looping the all the pairs of location updates until 1 has reach its end point
        while (n < allUpdate1.size() && m < allUpdate2.size()) {
            ArrayList<Location> updatePair1 = allUpdate1.get(n);
            ArrayList<Location> updatePair2 = allUpdate2.get(m);

            // retrieve all the location updates for both update pairs
            Location update1Location1 = updatePair1.get(0);
            Location update1Location2 = updatePair1.get(1);
            Location update2Location1 = updatePair2.get(0);
            Location update2Location2 = updatePair2.get(1);

            // retrieve all the timestamps for all location updates
            String update1Timestamp1 = update1Location1.getTimestamp();
            String update1Timestamp2 = update1Location2.getTimestamp();
            String update2Timestamp1 = update2Location1.getTimestamp();
            String update2Timestamp2 = update2Location2.getTimestamp();

            // retrieve the first locationId of both update pairs
            int update1LocationId1 = update1Location1.getLocationId();
            int update2LocationId1 = update2Location1.getLocationId();

            // retrieve the semantic-place of the first locations of updatePair1 and updatePair2
            String semanticPlace1 = "outsideSIS";
            String semanticPlace2 = "outsideSIS";

            // retrieve the locationlookup for a particular locationid, will return null if locationid = 0 and hence we assign it as "outsideSIS"
            LocationLookup locationLookup1 = locationLookupDAO.retrieve(update1LocationId1);
            LocationLookup locationLookup2 = locationLookupDAO.retrieve(update2LocationId1);

            // since there is an existing locationlookup in the database, we assign it to the semantic-place
            if (locationLookup1 != null) {
                semanticPlace1 = locationLookup1.getSemanticPlace();
            }

            if (locationLookup2 != null) {
                semanticPlace2 = locationLookup2.getSemanticPlace();
            }

            // instantianting the startDateTime and endDateTime to store the interception startDateTime and endDateTime
            String startDateTime = null;
            String endDateTime = null;

            // if the start of update pair 1 is before start of update pair 2, we just set the startDateTime as update pair2's start, vice-versa
            if (DateTimeUtility.compareDates(update1Timestamp1, update2Timestamp1) == -1) {
                startDateTime = update2Timestamp1;
            } else {
                startDateTime = update1Timestamp1;
            }

            // if the end of update pair 1 is after end of update pair 2, we just set the endDateTime as update pair2's end, vice-versa
            if (DateTimeUtility.compareDates(update1Timestamp2, update2Timestamp2) == 1) {
                endDateTime = update2Timestamp2;
                m++;
                // if both end points are the same, we would want to move the indexes of both together
            } else if (DateTimeUtility.compareDates(update1Timestamp2, update2Timestamp2) == 0) {
                endDateTime = update1Timestamp2;
                n++;
                m++;
            } else {
                endDateTime = update1Timestamp2;
                n++;
            }

            // only if the first locationID of both update pairs match and not == 0 (outside SIS) then we compute the time together
            if (!semanticPlace1.equals("outsideSIS") && semanticPlace1.equals(semanticPlace2)) {
                int diff = DateTimeUtility.getTimeDiffInSeconds(startDateTime, endDateTime);
                HashMap<String, Object> togetherEntry = new HashMap<String, Object>();
                togetherEntry.put("semantic-place", "" + semanticPlace1);
                togetherEntry.put("time-spent", new Integer(diff));
                togetherEntry.put("startDateTime", startDateTime);
                togetherEntry.put("endDateTime", endDateTime);
                nextPlaceList.add(togetherEntry);
            }
        }
        return nextPlaceList;
    }
}
