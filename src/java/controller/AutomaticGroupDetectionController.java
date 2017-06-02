package controller;

import dao.DemographicsDAO;
import dao.LocationLookupDAO;
import entity.Demographics;
import entity.Group;
import entity.GroupLocation;
import entity.GroupMember;
import entity.Location;
import entity.LocationLookup;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseConnectionManager;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;
import utility.TopKUtility;
/**
 * AutomaticGroupDetectionController class is used to contain the methods to detect groups
 *
 * @author Kenneth/Jingxiang
 */

public class AutomaticGroupDetectionController {

    /**
     * This contains a HashMap to store cached results for group computation. 
     * A simple true/false will denote if two members can be grouped together.
     */
    HashMap<String, Boolean> cachedResults;
    /**
     * Assigning a connection variable here, to be used for shared connection later on.
     */
    Connection conn;
    /**
     * DemographicsDAO to be used for retrieving mac addresses later
     */
    DemographicsDAO demographicsDAO;

    /**
     * Initializes cachedResults and DemographicsDAO
     */
    public AutomaticGroupDetectionController() {
        //We will initialize the cached results and demographicsDAO
        this.cachedResults = new HashMap<String, Boolean>();
        this.demographicsDAO = new DemographicsDAO();
        //Note the connection is not initalized here, but only when the methods are called.
    }

    /**
     * Retrieves a LinkedHashMap of Groups detected within a time span
     *
     * @return LinkedHashMap Returns a LinkedHashMap of Group Detection Results
     * consisting of total groups, total time spent, total users seen through
     * and each group details
     * @param date Specifies the current date time, and the current date time -
     * 15 minutes window to look for participants
     * @param token Specifies the token, to be validated
     * @param getLastSemanticPlace This will be set to true if Group Location
     * Report is calling this. Note that being set to true
     * @param fromJson This will be set to true if it is called by the Json Web
     * Services. If it is true, then token validation will ensure it comes from
     * the admin.
     */
    public LinkedHashMap getAutomaticGroupDetection(String date, String token, boolean getLastSemanticPlace, boolean fromJson) {
        //instantiate LinkedHashMap to store final result to return
        LinkedHashMap<String, Object> automaticGroupDetectionResult = new LinkedHashMap<String, Object>();


        //instantiate an ArrayList to store error messages (although there is only one validation for date, we would still wan to use an arraylist to capture future additional messages
        ArrayList<String> messages = new ArrayList<String>();


        if (date == null) {

            //Check for missing date
            messages.add(ErrorMessage.getMsg("missingDate"));
        } else if (date.length() == 0) {

            //Check for blank date
            messages.add(ErrorMessage.getMsg("blankDate"));

        } else {
            //check for valid date format (alphanumeric)
            if (!DateValidation.validateDateFormat(date)) {
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

        // if the error message list is empty, we would not want to continue anymore, hence, we are to return the result here
        if (!messages.isEmpty()) {

            // sort the errors according to alphabetical order
            Collections.sort(messages);

            automaticGroupDetectionResult.put("status", "error");
            automaticGroupDetectionResult.put("messages", messages);
            return automaticGroupDetectionResult;
        }

        // replacing the T in the datetimeformat to blank space
        String endDateTime = date.replace("T", " ");

        // retrieve the start datetime window (15 minutes ago) for the specified end datetime
        String startDateTime = DateTimeUtility.getTimeBefore(endDateTime, 15);

        //Declare an ArrayList of Group. A group basically contains an ArrayList of Demographics.
        ArrayList<Group> groupList = new ArrayList<Group>();

        //Retrieve a list of Stringwith their time pairing

        //Print start date (this is for debug, don't remove it - it comes in handy for optimization)
        System.out.println("Searching AGD between " + startDateTime + " to " + endDateTime);
        Date date1 = new Date();
        System.out.println("Start:" + date1.toString());

        LinkedHashMap<String, ArrayList<ArrayList<Location>>> userLocationPairs = TopKUtility.getAllLocationPairsMacAddress(startDateTime, endDateTime);
        int totalUsersSize = userLocationPairs.size();

        Iterator<ArrayList<ArrayList<Location>>> userLocationPairsIterator = userLocationPairs.values().iterator();
        int counterRemoved = 0;
        while (userLocationPairsIterator.hasNext()) {

            ArrayList<ArrayList<Location>> currentLocationPairs = userLocationPairsIterator.next();

            if (!canBeGrouped(currentLocationPairs, currentLocationPairs)) {
                userLocationPairsIterator.remove();
                counterRemoved++;
            }

        }


        //System.out.println("The size of LinkedHashMap:" + userLocationPairs.size());
        //Clone it as there will be recursive comparison
        LinkedHashMap<String, ArrayList<ArrayList<Location>>> userLocationPairsClone = (LinkedHashMap) userLocationPairs.clone();
        try {
            this.conn = DatabaseConnectionManager.getConnection();


            //This starts the counter and will prevent double counting for the cloned userLocationPairs
            //for instance
            // In userLocationPairs
            // Mac Address               [macAddress1]         [macAddress2]           [macAddress3]
            // value of externalCounter  [     0     ]         [     1     ]           [     3      ]
            // the value of externalCounter increases while iterate through the first userLocationPairs
            int externalCounter = 0;
            for (Map.Entry<String, ArrayList<ArrayList<Location>>> entry : userLocationPairs.entrySet()) {

                //Retrieve demographics of the user
                String macAddress = entry.getKey();

                //Retrieve all the pairings of this particular user
                ArrayList<ArrayList<Location>> locationList = (ArrayList) entry.getValue();

                //The inner counter is basically counter for the userLocationPairs and to prevent double counting.
                int innerCounter = 0;

                //Consider this
                // MacAddress                [macAddress1]         [macAddress2]           [macAddress3]
                // value of externalCounter  [  0   ]         [   1   ]           [  2   ]
                // Value of internalCounter  [  0   ]         [   1   ]           [  2   ]
                //Take for instance, if value of externalCounter is 1
                //When you are inside internalCounter, you will not want to count anything smaller than 1
                //Why?
                //If value of externalCounter is 1
                //It means mac Address 1 has been iterated through, and has been checked for a group in this manner:
                //macAddress1 - macAddress2
                //macAddress1 - macAddress3
                //Therefore, now if externalCounter is 1 (meaning it is at macAddress2),
                //1) You do not need to check for the macAddress1-macAddress2 pair
                //2) You do not need to check for the macAddress2-macAddress2 pair
                //Hence the condition 'if (innerCounter >= externalCounter) {'
                //Reiterate through the second userLocationPairs
                for (Map.Entry<String, ArrayList<ArrayList<Location>>> entryClone : userLocationPairsClone.entrySet()) {

                    //Retrieve the second user for checking
                    String macAddressClone = entryClone.getKey();

                    //Get the time pairings for the second user
                    ArrayList<ArrayList<Location>> locationListClone = (ArrayList) entryClone.getValue();

                    //Only checked the ones that have not been checked
                    if (innerCounter >= externalCounter) {
                        //Let's skip if the mac address is the same. It means you are comparing (for e.g.) macAddress1 vs macAddress1
                        if (macAddress.equals(macAddressClone)) {
                            //Continue to check for the rest
                            continue;
                        } else {
                            //If both user has spent at least 720 seconds together, let's add them as a group.
                            if (canBeGrouped(locationList, locationListClone)) {
                                addGroups(macAddress, macAddressClone, groupList, userLocationPairs, getLastSemanticPlace);
                            }
                        }
                    }
                    //Add inner counter
                    innerCounter++;
                }
                //Add external counter
                externalCounter++;
            }
            //This method sorts the entire group locations report. Basically what it does is that, for instance
            //If user A and B spent 800 seconds together in Location A and 
            //If user B and C spent 720 seconds together in Location B, 
            //this method ensures 720 seconds is the official record listed as the group's time spent together (the least time)        
            sortGroupsLocationsRecordsAndMembers(groupList, userLocationPairs);

            //This method sorts the group by total size, and total time spent together (using Group as an entity and comparable)
            Collections.sort(groupList);

            //We prepare an array list of linked hashmap to return the group data is the format required 
            ArrayList<LinkedHashMap<String, Object>> returnGroups = new ArrayList<LinkedHashMap<String, Object>>();

            //At this stage, we can assume the request is successful
            automaticGroupDetectionResult.put("status", "success");

            //Return the total size of the group
            automaticGroupDetectionResult.put("total-groups", groupList.size());

            //Return the total number of users spent in the locatino
            automaticGroupDetectionResult.put("total-users", totalUsersSize);

            //Reiterate through the ArrayList of Group and store the necessary
            for (Group g : groupList) {
                //Initialize a LinkedHashmap of result for one group
                LinkedHashMap<String, Object> groupResult = new LinkedHashMap<String, Object>();

                //Return the size of this group
                groupResult.put("size", g.getGroupMembers().size());

                //Return the total time spent for this group
                groupResult.put("total-time-spent", g.getTotalTimeSpent());

                //Return all the members email and demographics if any
                groupResult.put("members", g.getGroupMembersList());

                //Return the locations of the group
                groupResult.put("locations", g.getGroupLocationsList());


                //If asked to get last semantic place, then we return the necessary. Usually, this is for Group Location Reports
                if (getLastSemanticPlace) {
                    //Return the locations of the group
                    groupResult.put("lastSemanticPlace", g.getLastSemanticPlace());
                    groupResult.put("lastSemanticPlaceEndDateTime", g.getLastSemanticPlaceEndDateTime());
                }
                //Add to array list of Group Results
                returnGroups.add(groupResult);
            }
            //Adds entire arraylist of Group LinkedHashedMap into the main LinkedHashmap to be returned 

            automaticGroupDetectionResult.put("groups", returnGroups);

            //Print the end date time
            Date date2 = new Date();
            System.out.println("End:" + date2.toString());
        } catch (SQLException ex) {
            Logger.getLogger(AutomaticGroupDetectionController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DatabaseConnectionManager.closeConnection(conn);
        }


        //Returns the entire LinkedHashMap of results
        return automaticGroupDetectionResult;

    }

    /**
     * Decides if two user's timing can be grouped together
     * @return true if both users' arraylist of locations have at least 720 time spent together
     * @param locationList1 Contains the list of location lookups by first user
     * @param locationList2 Contains the list of location lookups by second user
     */
    public boolean canBeGrouped(ArrayList<ArrayList<Location>> locationList1, ArrayList<ArrayList<Location>> locationList2) {

        //Call Top K Utility method to compute the total time together
        //It returns an ArrayList of HashMap
        //The HashMap looks like this
        //[  String  ] [  String  ]
        //[ locationid ] : [ 5 ]
        //[ time-spent ] : [ 250 ] (250 is seconds)
        //[ startDateTime ] : [ 2014-10-20 09:00:00 ] 
        //[ endDateTime ] : [ 2014-10-20 10:00:00 ] 
        ArrayList<HashMap<String, String>> computeTimeTogether = TopKUtility.computeTotalTimeTogether(locationList1, locationList2);
        int totalTimeSpent = 0;

        //If ArrayList of Hashmap is not empty
        if (computeTimeTogether.size() > 0) {

            //Iterate through the ArrayList of HashMap
            for (HashMap<String, String> timeSpentTogether : computeTimeTogether) {
                //Get time from the Hashmap and add to totaltimespent
                totalTimeSpent += Integer.parseInt(timeSpentTogether.get("time-spent"));
            }
        }

        //If we ever need to change the time spent together from 12 minutes to another time, we change the value below.
        //Return true if user spent at least 720 seconds (12 minutes) together.
        return (totalTimeSpent >= 720);

    }

    /**
     * Two users have an acceptable threshold of time spent together and this
     * method attempts to add them as a group. This method is pretty
     * sophisticated whereby it tries to match them to an already existing group
     * with group members with similar timings. Failing which, it will add them
     * as an entirely new group.
     *
     * @param m1 This is the first mac address to be added
     * @param m2 This is the second mac address to be added
     * @param groupList This is the current ArrayList of groups (we need this to see if the user can be fitted into an existing time slot)
     * @param userLocationPairs This contains all the users' locations time. A user's timings can be retrieved using mac address.
     */
    public void addGroups(String m1, String m2, ArrayList<Group> groupList, LinkedHashMap<String, ArrayList<ArrayList<Location>>> userLocationPairs, boolean getLastSemanticPlace) {

        //Let's check if the first mac address has an email
        Demographics demographics = demographicsDAO.retrieveByMacAddress(this.conn, m1);
        //Set the email by default to "", and it will remain this way if the first mac address cannot be found in Demographics
        String email = "";

        //First mac address can be found in Demographics table
        if (demographics != null) {
            //Hence, let's set his email
            email = demographics.getEmail();
        }
        //Initialize a Group Member object with the first mac address
        GroupMember gm1 = new GroupMember(email, m1);

        //Let's check if the second mac address has an email
        demographics = demographicsDAO.retrieveByMacAddress(this.conn, m2);

        //Set the email by default to "", and it will remain this way if the second mac address cannot be found in Demographics
        email = "";

        //If second mac address can be found in Demographics table
        if (demographics != null) {

            //Hence, let's set his email
            email = demographics.getEmail();
        }
        //Initialize a Group member object with the second member email address
        GroupMember gm2 = new GroupMember(email, m2);



        //Depicts if the first member and second member have been added.
        boolean firstAdd = false;
        boolean secondAdd = false;


        //Retrieve the two user time pairings for both user
        ArrayList<ArrayList<Location>> groupMember1 = (ArrayList) userLocationPairs.get(m1);
        ArrayList<ArrayList<Location>> groupMember2 = (ArrayList) userLocationPairs.get(m2);

        //If there are existing group, then let's run through them to see if this matching pair can be fit into any of the groups
        if (groupList.size() > 0) {
            //Do the first check
            for (Group g : groupList) {
                //Get group members record for faster processing
                HashMap<String, GroupMember> groupMembersRecord = g.getGroupMembersRecord();

                //Assume user can be added to this group first
                boolean canAddToGroup = true;

                //Let's get the list of group members to compare
                ArrayList<GroupMember> currentGroupList = g.getGroupMembers();

                //Declare an ArrayList to retrieve the pairings for each group member being compared
                ArrayList<ArrayList<Location>> groupMemberCompare;

                //Let's iterate through group member to compare
                for (GroupMember gm : currentGroupList) {
                    if (groupMembersRecord.get(m1) != null) {
                        //Hence, the first user does not belong to this group

                        //No need to add him to this particular group being iterated since he already exists in here
                        canAddToGroup = false;
                        break;
                    }

                    //Match the member in the existing group members
                    groupMemberCompare = (ArrayList) userLocationPairs.get(gm.getMacAddress());

                    //This returns true if both user has spent at least 12 minutes
                    boolean canBeGrouped;

                    //This attempts to look for a cached result (to save time computing)
                    //For instance, it will store in a LinkedHashMap
                    // [String of MacAddress1-MacAddress2] : [ Boolean value if they can be grouped together]
                    // Examples of a real life value looks like:
                    // [macAddress1-macAddress2] : [true]
                    // [macAddress1-macAddress3] : [false]


                    if (cachedResults.get(gm1 + "-" + gm.getMacAddress()) != null) {
                        canBeGrouped = cachedResults.get(gm1 + "-" + gm.getMacAddress());
                    } else if (cachedResults.get(gm.getMacAddress() + "-" + gm1) != null) {
                        canBeGrouped = cachedResults.get(gm.getMacAddress() + "-" + gm1);
                    } else {
                        //If can't find any cached value, too bad.. do the computing yourself
                        canBeGrouped = canBeGrouped(groupMember1, groupMemberCompare);

                        //Now store the value into cached results so you don't have to redo it next time :)
                        cachedResults.put(gm1 + "-" + gm.getMacAddress(), canBeGrouped);
                    }
                    if (!canBeGrouped) {
                        //Set him to failing a group check

                        //No need to add him to this group since he mismatches one of the member
                        canAddToGroup = false;
                        break;
                    }

                }
                //At the end of iterating through the for loop, you wil lbe able to see if any 
                //Passed all checks, so user can be added to this group :)
                if (canAddToGroup) {
                    currentGroupList.add(gm1);

                    groupMembersRecord.put(m1, gm1);

                    firstAdd = true;

                }
            }
            //Do the second check
            for (Group g : groupList) {
                //Get group members record for faster processing
                HashMap<String, GroupMember> groupMembersRecord = g.getGroupMembersRecord();

                //Assume user can be added to this group first
                boolean canAddToGroup = true;

                //Let's get the list of group members to compare
                ArrayList<GroupMember> currentGroupList = g.getGroupMembers();

                //Declare an ArrayList to retrieve the pairings for each group member being compared
                ArrayList<ArrayList<Location>> groupMemberCompare;

                //Let's iterate through group member to compare
                for (GroupMember gm : currentGroupList) {
                    if (groupMembersRecord.get(m2) != null) {
                        //Hence, the first user does not belong to this group

                        //No need to add him to this particular group being iterated since he already exists in here
                        canAddToGroup = false;
                        break;
                    }

                    //Match the member in the existing group members
                    groupMemberCompare = (ArrayList) userLocationPairs.get(gm.getMacAddress());

                    //This returns true if both user has spent at least 12 minutes
                    boolean canBeGrouped;
                    if (cachedResults.get(gm2 + "-" + gm.getMacAddress()) != null) {
                        canBeGrouped = cachedResults.get(gm2 + "-" + gm.getMacAddress());
                        //System.out.println("Results1");
                    } else if (cachedResults.get(gm.getMacAddress() + "-" + gm2) != null) {
                        canBeGrouped = cachedResults.get(gm.getMacAddress() + "-" + gm2);
                        //System.out.println("Results2");
                    } else {
                        canBeGrouped = canBeGrouped(groupMember2, groupMemberCompare);
                        cachedResults.put(gm2 + "-" + gm.getMacAddress(), canBeGrouped);
                        //System.out.println("Results3");
                    }
                    if (!canBeGrouped) {
                        //Set him to failing a group check

                        //No need to add him to this group since he mismatches one of the member
                        canAddToGroup = false;
                        break;
                    }

                }
                //At the end of iterating through the for loop, you wil lbe able to see if any 
                //Passed all checks, so user can be added to this group :)
                if (canAddToGroup) {
                    currentGroupList.add(gm2);


                    groupMembersRecord.put(m2, gm2);
                    secondAdd = true;


                }
            }

        }
        //If both are set to false, it means they have not been added in groups before
        if (!firstAdd && !secondAdd) {
            //System.out.println("New group: " + m1 + " - " + m2);
            //Assume this two users do not have any groups added
            boolean foundGroups = false;


            //If groupsize is bigger than 0, let's check if any groups contains the two members. If there is, no point adding.
            if (groupList.size() > 0) {
                //Iterate through group now to see if they exist in any group
                for (Group g : groupList) {
                    HashMap<String, GroupMember> groupMembersRecord = g.getGroupMembersRecord();

                    //If they are in an existing group.
                    if (groupMembersRecord.get(m1) != null && groupMembersRecord.get(m2) != null) {
                        //System.out.println("Failed group: " + m1 + " - " + m2);
                        //Set foundGroups to true and break out of loop
                        foundGroups = true;
                        break;
                    }
                }
            }

            //If d1 and d2 are not in any existing group, let's add them as a group.
            if (!foundGroups) {

                //Initialize an ArrayList of GroupMember
                ArrayList<GroupMember> groupMembersList = new ArrayList<GroupMember>();

                //Add Group Members into this Array List
                groupMembersList.add(gm1);
                groupMembersList.add(gm2);

                //Initialize new Group
                Group newGroup = new Group();
                //Get group members record for faster processing
                HashMap<String, GroupMember> groupMembersRecord = newGroup.getGroupMembersRecord();

                //Let's add the records of this group member into a mac address
                groupMembersRecord.put(m1, gm1);
                groupMembersRecord.put(m2, gm2);

                //Set group members into the new group
                newGroup.setGroupMembers(groupMembersList);

                //Retrieve the combined location and time spent of group member 1 and group member 2
                LinkedHashMap<String, Integer> groupLocationRecords = getLocationAndTimeSpent(groupMember1, groupMember2);

                //If required, then we set the last semantic place and time
                //Usually this is for location reports
                if (getLastSemanticPlace) {
                    ArrayList<String> lastSemanticPlace = getLastLocation(groupMember1, groupMember2);
                    newGroup.setLastSemanticPlace(lastSemanticPlace.get(0));
                    newGroup.setLastSemanticPlaceEndDateTime(lastSemanticPlace.get(1));
                }

                //Set the location records into the new group
                newGroup.setLocationRecords(groupLocationRecords);

                //Add the group into the ArrayList of Group
                groupList.add(newGroup);
                Date date = new Date();

                //System.out.println("Size of groups are now:" + groupList.size() + " - " + date.toString());

            }


        }


    }

    /**
     * This method sorts the entire group locations report. For instance, user A
     * and B spent 800 seconds together in Location A and If user B and C spent
     * 720 seconds together in Location B, this method ensures 720 seconds is
     * the official record listed as the group's time spent together (the least
     * time)
     *
     * This was not done when adding groups, to decouple the logic from the
     * group adding algorithm. As it is, the group adding algorithm is very
     * complex and may be subject to change. Even though adding a method here
     * makes it slightly less efficient in terms of performance, since it does
     * an extra step of sorting the location report only at the end, it will go
     * a long way in maintaining and debugging if ever needed.
     *
     *
     * @param groupList This is the ArrayList of Group to be sorted
     * @param userLocationPairs This contains the entire record of user timings
     * (we need it as we sort the data in the Group to minimize the location
     * records)
     */
    public void sortGroupsLocationsRecordsAndMembers(ArrayList<Group> groupList, LinkedHashMap<String, ArrayList<ArrayList<Location>>> userLocationPairs) {
        //Let's start to iterate through the Group
        for (Group g : groupList) {

            //Declare an ArrayList of GroupMembers (from each group), and retrieve the group members
            ArrayList<GroupMember> groupMembersList = g.getGroupMembers();

            //Declare an ArrayList of GroupMembers (from each group), and clone it
            //This is because we need to compare the list of members
            ArrayList<GroupMember> groupMembersListClone = (ArrayList) groupMembersList.clone();

            //Retrieve the group location reports, we need this in order to check against the minimum time spent at a location.
            LinkedHashMap<String, Integer> groupLocationRecords = g.getLocationRecords();

            //Retrieve an ArrayList of Group Location
            ArrayList<GroupLocation> groupLocationList = new ArrayList<GroupLocation>();

            int externalCounter = 0;

            //Reiterate through each group member
            //The algorithm basically works this way
            //groupMembersList [ Kenneth ] [ Eugene ] [ Jiacheng ] [ Jingxiang ] [ Jocelyn ]            
            //groupMembersListClone [ Kenneth ] [ Eugene ] [ Jiacheng ] [ Jingxiang ] [ Jocelyn ]       
            //For each time it iterates through groupMembersList, it will: 
            // 1) do an externalCounter++;
            // 2) internalCount=0
            // 3) iterate through groupMembersListClone
            //For each time it iterates through groupMembersListClone, it will do the following
            // 1) internalCount++
            //Hence the algorithm goes like this
            //Kenneth is the first object in groupmembersList, and will check in the following manner
            //Kenneth - Kenneth (NOT CHECKED), because of the if (internalCounter > externalCounter) --> at this stage, externalCounter will be 0 (Kenneth), and innerCounter will be 0 (Kenneth)
            //Kenneth-Eugene (checked)
            //Kenneth-Jiacheng (checked)
            //Kenneth-Jingxiang (checked)
            //Kenneth-Jocelyn (checked)
            //Next, when it reaches Eugene, it will checking in the following manner
            //Eugene-Kenneth  (NOT CHECKED), because of the if (internalCounter > externalCounter) --> at this stage, externalCounter will be 1 (Eugene), and innerCounter will be 0 (Kenneth)
            //Eugene-Eugene  (NOT CHECKED), because of the if (internalCounter > externalCounter) --> at this stage, externalCounter will be 1 (Eugene), and innerCounter will be 1 (Eugene)
            //Eugene-Jiacheng (checked)
            //Eugene-Jingxiang (checked)
            //Eugene-Jocelyn (checked)
            //^Why does it not check Eugene-Kenneth, because it already checked Kenneth-Eugene as a pair earlier on.
            //Reiteraes through an ArrayList of Group Members
            for (GroupMember gm : groupMembersList) {
                int internalCounter = 0;
                //Reiterates through an ArrayList of Group Members Clone
                for (GroupMember gmClone : groupMembersListClone) {

                    //The algorithm is explained in huge details above
                    if (internalCounter > externalCounter) {

                        //Retrieve the group members locations and timing
                        ArrayList<ArrayList<Location>> gmFirstPair = (ArrayList) userLocationPairs.get(gm.getMacAddress());
                        ArrayList<ArrayList<Location>> gmSecondPair = (ArrayList) userLocationPairs.get(gmClone.getMacAddress());

                        //Retrieve the comparison between two pairs in a LinkedHashMap
                        //The LinkedHashMap contains data as follows
                        // [ String ] : [ Integer ]
                        // [ 1001001001 ] : [ 720 ] (this is the total time spent together at location id 1001001001)
                        // [ 1001001002 ] : [ 240 ] (this is the total time spent together at location id 1001001001)                        
                        LinkedHashMap<String, Integer> comparisonTiming = getLocationAndTimeSpent(gmFirstPair, gmSecondPair);

                        //Reitetate through every entry
                        for (Map.Entry<String, Integer> entry : comparisonTiming.entrySet()) {

                            //Set the location id and the time spent from the key and the value
                            String locationId = entry.getKey();
                            int timeSpent = entry.getValue();

                            //Retrieve the time spent from the group location records
                            if (groupLocationRecords.get(locationId) != null) {
                                int groupTimeSpent = groupLocationRecords.get(locationId);

                                //If the time spent as a pair is lesser than the group's, then we need to replace the group's record
                                if (groupTimeSpent > timeSpent) {
                                    //Replace the record
                                    groupLocationRecords.put(locationId, timeSpent);
                                }
                            }

                        }
                    }

                    internalCounter++;

                }
            }

            externalCounter++;

            //Let's add all the group location reports into a new ArrayList
            //Reiterate through the Group Location Records LinkedHashMap
            for (Map.Entry<String, Integer> entry : groupLocationRecords.entrySet()) {
                GroupLocation groupLocation = new GroupLocation(entry.getKey(), entry.getValue());
                groupLocationList.add(groupLocation);
            }
            //Set groupLocationList into the group
            g.setGroupLocations(groupLocationList);

            //Let's sort the group location list (this is a much better way to sort, using comparables)
            Collections.sort(groupLocationList);


            //Let's sort the group members.
            Collections.sort(g.getGroupMembers());

        }

    }

    /**
     * This method compares the time spent of two users. Following, it returns a <br>
     * LinkedHashMap containing the location id and TOTAL time spent together at <br>
     * that location  <br>
     * Example: <br>
     * ["1001001001"] : [650]  <br>
     * ["1001001002"] : [60] <br>
     * <br>
     * @param userTimes1 Contains all the location timing pairs of the first user
     * @param userTimes2 Contains all the location timing pairs of the second user
     * @return LinkedHashMap<String,Integer> of common location id and total time spent together between user 1 and 2
     */
    public LinkedHashMap<String, Integer> getLocationAndTimeSpent(ArrayList<ArrayList<Location>> userTimes1, ArrayList<ArrayList<Location>> userTimes2) {

        //Declare LinkedHashMap to be returned later
        LinkedHashMap<String, Integer> locationAndTimeSpent = new LinkedHashMap<String, Integer>();

        //Retrieve information of the times where both users spent time together
        ArrayList<HashMap<String, String>> computeTimeTogether = TopKUtility.computeTotalTimeTogether(userTimes1, userTimes2);

        //If there is at least 1 location where the users spent time together
        if (computeTimeTogether.size() > 0) {

            //Reiterate through the HashMap of times
            for (HashMap<String, String> timeSpentTogether : computeTimeTogether) {

                //Get Location ID
                String locationId = (String) timeSpentTogether.get("location");

                //Let's check in the LinkedHashMap if there is a existing locationid
                //If there is none, then let's proceed to add it in ourselves 
                if (locationAndTimeSpent.get(locationId) == null) {
                    //Let's set the time spent together
                    locationAndTimeSpent.put(locationId, Integer.parseInt(timeSpentTogether.get("time-spent")));
                } else {
                    //Else, let's add to the cumulative time spent.
                    int timeSpentSoFar = Integer.parseInt(timeSpentTogether.get("time-spent")) + (Integer) locationAndTimeSpent.get(locationId);

                    //Then overwrite the existing record
                    locationAndTimeSpent.put(locationId, timeSpentSoFar);

                }

            }
        }
        //Return the results
        return locationAndTimeSpent;
    }
    /**
     * This method returns an ArrayList of two strings containing last semantic place and the datetime two users were at.
     * It is only invoked when the Group Location Reports are required (to save some processing time). 
     * @return Returns an ArrayList of String containing Semantic Place (first element) and Date Time (second element).
     */

    public ArrayList<String> getLastLocation(ArrayList<ArrayList<Location>> demographics1, ArrayList<ArrayList<Location>> demographics2) {
        //Set the last location id to 0
        int lastLocationId = 0;

        //Initialize current date
        Date currentDate = new Date();

        //Set the format 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Set current date to 1980-01-01 00:00:00
            currentDate = sdf.parse("1980-01-01 00:00:00");
        } catch (ParseException ex) {
        }

        //Get computed time together
        ArrayList<HashMap<String, String>> computeTimeTogether = TopKUtility.computeTotalTimeTogether(demographics1, demographics2);

        //If there is time spent together
        if (computeTimeTogether.size() > 0) {

            //Reiterate through time spent together to find the latest date
            for (HashMap<String, String> timeSpentTogether : computeTimeTogether) {
                
                //Get the location id
                String locationId = (String) timeSpentTogether.get("location");
                try {
                    //Get end date time to compare
                    Date compareDate = sdf.parse((String) timeSpentTogether.get("endDateTime"));

                    //If current date is earlier than end date, then store the end date and the locationid
                    if (compareDate.after(currentDate)) {

                        currentDate = compareDate;
                        lastLocationId = Integer.parseInt((String) timeSpentTogether.get("location"));

                    }
                } catch (ParseException ex) {
                    Logger.getLogger(AutomaticGroupDetectionController.class.getName()).log(Level.SEVERE, null, ex);
                }


            }
        }
        //At this point, we would have found our latest place together.

        //Initialize LocationLookupDAO
        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();

        //Let's retrieve the Semantic Place
        LocationLookup locationLookup = locationLookupDAO.retrieve(lastLocationId);

        //Initialize the strings to return
        ArrayList<String> returnString = new ArrayList<String>();
        //Add semantic place
        returnString.add(locationLookup.getSemanticPlace());
        //Add the current date in YYYY-MM-DD HH:mm:ss format.
        returnString.add(sdf.format(currentDate));
        return returnString;
    }
}
