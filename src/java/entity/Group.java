package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Group class (created for the sake of organization and to assist with
 * comparing the group size and total time spent) Group has an ArrayList of
 * GroupMember, ArrayList of GroupLocation, and a LinkedHashMap of Location
 * Records
 *
 * @author Jingxiang
 */
public class Group implements Comparable<Group> {

    //Stores an ArrayList of Group Members
    ArrayList<GroupMember> groupMembers;
    //Stores an ArrayList of GroupLocation
    ArrayList<GroupLocation> groupLocations;
    //Stores the location records of where the members have been
    LinkedHashMap<String, Integer> locationRecords;
    //Stores the mac address of the group member, and the group member object
    //This saves time later on for retrieval, as there is no need to iterate through the ArrayList of GroupMember
    HashMap<String, GroupMember> groupMembersRecord;
    //These two variables are used to store the last semantic place and datetime of the group, 
    String lastSemanticPlace;
    String lastSemanticPlaceEndDateTime;

    /**
     * Creates a Group Object and initializes groupMembersRecord.
     */
    public Group() {
        //Initialize only the group member records hashmap.
        this.groupMembersRecord = new HashMap<String, GroupMember>();
    }

    /**
     * Returns the ArrayList of GroupLocation
     *
     * @return returns an arraylist of GroupLocation
     */
    public ArrayList<GroupLocation> getGroupLocations() {
        return groupLocations;
    }

    /**
     * This method is used to return ArrayList of String specifically for the
     * Json Webservice/UI to process <br>
     * It retrieves information from the ArrayList of GroupLocation and
     * processes them into LinkedHashMap of objects <br>
     * The LinkedHashMap typically stores data of locationid and time spent <br>
     * Example: <br>
     * [ "1001001001" ] : [ 240 ] (total amount in seconds) <br>
     * [ "1001001002" ] : [ 480 ] <br>
     * [ "1001001003" ] : [ 540 ] <br>
     *
     * @return returns an arraylist of GroupLocation
     */
    public ArrayList<LinkedHashMap<String, Object>> getGroupLocationsList() {

        //Declare an ArrayList of LinkedHashMap to be returned
        ArrayList<LinkedHashMap<String, Object>> groupLocationsList = new ArrayList<LinkedHashMap<String, Object>>();

        //Let's start to reiterate through the ArrayList of GroupLocation
        for (GroupLocation gl : groupLocations) {

            //Initialize one LinkedHashMap to store ONE group's result
            LinkedHashMap<String, Object> groupLocationReturn = new LinkedHashMap<String, Object>();

            //Set the location id and time-spent into the LinkedHashMap
            groupLocationReturn.put("location", gl.getLocationId());
            groupLocationReturn.put("time-spent", gl.getTimeSpent());


            groupLocationsList.add(groupLocationReturn);
        }
        return groupLocationsList;
    }

    /**
     * Sets the LinkedHashMap<String,Object> into groupLocations <br>
     * The LinkedHashMap typically stores data of locationid and time spent <br>
     * Example: <br>
     * [ 1001001001 ] : [ 240 ] (total amount in seconds) <br>
     * [ 1001001002 ] : [ 480 ] <br>
     * [ 1001001003 ] : [ 540 ] <br>
     *
     * @param sets ArrayList of GroupLocation
     */
    public void setGroupLocations(ArrayList<GroupLocation> groupLocations) {
        this.groupLocations = groupLocations;
    }

    /**
     * Retrieves the LinkedHashMap<String,Object> of location records <br>
     * The LinkedHashMap typically stores data of locationid and time spent <br>
     * Example: <br>
     * [ 1001001001 ] : [ 240 ] (total amount in seconds) <br>
     * [ 1001001002 ] : [ 480 ] <br>
     * [ 1001001003 ] : [ 540 ] <br>
     *
     * @param sets LinkedHashMap<String,Integer> containing location id and time
     * spent
     */
    public LinkedHashMap<String, Integer> getLocationRecords() {
        return locationRecords;
    }

    /**
     * Sets the LinkedHashMap<String,Object> into location records <br>
     * The LinkedHashMap typically stores data of locationid and time spent <br>
     * Example: <br>
     * [ 1001001001 ] : [ 240 ] (total amount in seconds) <br>
     * [ 1001001002 ] : [ 480 ] <br>
     * [ 1001001003 ] : [ 540 ] <br>
     *
     * @return sets LinkedHashMap<String,Integer> containing location id and
     * time spent <br>
     */
    public void setLocationRecords(LinkedHashMap<String, Integer> locationRecords) {
        this.locationRecords = locationRecords;
    }

    /**
     * Returns an ArrayList of GroupMember (will contain at least 2 members,
     * otherwise Group will not be initialized in the first place).
     *
     * @return ArrayList of GroupMember
     */
    public ArrayList<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    /**
     * This method is used to return ArrayList of LinkedHashMap<String,String>
     * specifically for the Json Webservice/UI to process <br>
     * It retrieves information from the ArrayList of GroupMember and processes
     * them into LinkedHashMap of objects <br>
     * The LinkedHashMap typically stores data of email and macaddress <br>
     * Example: <br>
     * [ "email" ] : [ "jocelyn.ng.2012
     *
     * @sis.smu.edu.sg" ] (this value can be blank if the user is not in
     * Demographics table) <br>
     * [ "mac-address" ] : [ "abcdef1234abcdef1234abcdef1234abcdef1234" ] (this
     * value can be blank if the user is not in Demographics table) <br>
     * @return returns an arraylist of LinkedHashMap<String,String>
     */
    public ArrayList<LinkedHashMap<String, String>> getGroupMembersList() {
        //Declare an ArrayList of LinkedHashMap<String,String> to be returned later on
        ArrayList<LinkedHashMap<String, String>> groupMembersList = new ArrayList<LinkedHashMap<String, String>>();

        //Iterate through the ArrayList of Group members
        for (GroupMember gm : groupMembers) {

            //Initialize a LinkedHashMap to store the data
            LinkedHashMap<String, String> groupMemberReturn = new LinkedHashMap<String, String>();

            //Store email and mac address results into the LinkedHashMap
            groupMemberReturn.put("email", gm.getEmail());
            groupMemberReturn.put("mac-address", gm.getMacAddress());

            //Add to ArrayList
            groupMembersList.add(groupMemberReturn);
        }
        //Return the results
        return groupMembersList;
    }

    /**
     * Sets an ArrayList of GroupMember (likely to contain at least 2 members,
     * otherwise Group will not be initialize).
     *
     * @param set ArrayList of GroupMember
     */
    public void setGroupMembers(ArrayList<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     * This method is used to calculate and return the total time spent based on
     * the location records LinkedHashMap <br>
     * The LinkedHashMap typically stores data of location id and time spent
     * <br>
     * For, example: <br>
     * [ locationid ] : [ time-spent] <br>
     * [ 1001001001 ] : [ 5 ] <br>
     * [ 1001001001 ] : [ 6 ] <br>
     * [ 1001001001 ] : [ 7 ] <br>
     * This method will return the sum of 5 + 6 +7
     *
     * @return returns the total time spent together
     */
    public int getTotalTimeSpent() {

        //Set counter to 0
        int totalTimeSpent = 0;

        //Reiterate through a LinkedHashMap of location records
        for (Map.Entry<String, Integer> entry : this.locationRecords.entrySet()) {

            //Add the time spent to counter
            totalTimeSpent += entry.getValue();
        }

        //Return counter
        return totalTimeSpent;
    }

    /**
     * This method is used to search if a certain mac address matches that of an
     * existing group member.
     *
     * @return true if mac address exists, false if otherwise
     */
    public boolean findMacAddress(String macAddress) {
        //Iterate through GroupMember
        for (GroupMember gm : getGroupMembers()) {
            //If the mac address matches
            if (gm.getMacAddress().equals(macAddress)) {
                //Return true, no need to search any further
                return true;
            }
        }
        //At this stage, return false cause not found
        return false;
    }

    /**
     * Returns the last semantic place of the group
     *
     * @return the last semantic place of the group
     */
    public String getLastSemanticPlace() {
        return this.lastSemanticPlace;
    }

    /**
     * Sets the last semantic place of the group
     *
     * @param lastSemanticPlace the last semantic place of the group
     */
    public void setLastSemanticPlace(String lastSemanticPlace) {
        this.lastSemanticPlace = lastSemanticPlace;
    }

    /**
     * Returns the date time spent at the last semantic place of the group
     *
     * @return the date time spent at the last semantic place of the group
     */
    public String getLastSemanticPlaceEndDateTime() {
        return this.lastSemanticPlaceEndDateTime;
    }

    /**
     * Sets the last date time spent at the last semantic place of the group
     *
     * @param lastSemanticPlaceEndDateTime the date time spent at the last
     * semantic place of the group
     */
    public void setLastSemanticPlaceEndDateTime(String lastSemanticPlaceEndDateTime) {
        this.lastSemanticPlaceEndDateTime = lastSemanticPlaceEndDateTime;
    }

    /**
     * Returns a HashMap of <MacAddress, GroupMember> record This will speed up
     * processing and it's faster to check for existing mac addresses using
     * HashMap than to iterate through an ArrayList
     *
     * @return HashMap<String, GroupMember> Returns a HashMap of <MacAddress,
     * GroupMember> record
     */
    public HashMap<String, GroupMember> getGroupMembersRecord() {
        return groupMembersRecord;
    }

    /**
     * Sets HashMap of GroupMember record. THe string denotes the mac address,
     * and the GroupMember is the object stored.
     *
     * @param groupMembersRecord HashMap of Group Member record.
     */
    public void setGroupMembersRecord(HashMap<String, GroupMember> groupMembersRecord) {
        this.groupMembersRecord = groupMembersRecord;
    }

    /**
     * Used to compare Group position. Size is used as the measuring yardstick.
     * If size is the same, then total group time is compared.
     *
     * @return position -1, 0 or -1
     */
    public int compareTo(Group o) {
        //Get the current and comparing group list of group members 
        ArrayList<GroupMember> currentGroup = this.getGroupMembers();
        ArrayList<GroupMember> comparingGroup = o.getGroupMembers();

        //If current group has more group members than the comparing group
        if (currentGroup.size() > comparingGroup.size()) {
            return -1;
        } else if (currentGroup.size() < comparingGroup.size()) {
            //If current group has less group members than the comparing group
            return 1;
        } else {
            //Here, it is deemed that the number of group members appears to be equal
            //So we are left with no choice but to compare the total time spent

            //We get the time spent together for both the current and comparing group
            int currentGroupTimeSpent = this.getTotalTimeSpent();
            int comparingGroupTimeSpent = o.getTotalTimeSpent();

            //If the current group spent more time together than the comparing group
            if (currentGroupTimeSpent > comparingGroupTimeSpent) {
                return -1;
            } else if (currentGroupTimeSpent < comparingGroupTimeSpent) {
                //If the current group spent less time together than the comparing group
                return 1;
            } else {
                //Else they are equal

                //Let's compare the group members
                for (int i=0;i< currentGroup.size();i++) {
                    GroupMember firstGroupMember=currentGroup.get(i);
                    GroupMember secondGroupMember=comparingGroup.get(i);
                    String currentMacAddress=firstGroupMember.getMacAddress();
                    String comparingMacAddress=secondGroupMember.getMacAddress();
                    if (currentMacAddress.compareToIgnoreCase(comparingMacAddress) > 0) {
                        return 1;
                    } else if (currentMacAddress.compareToIgnoreCase(comparingMacAddress) < 0) {
                        return -1;
                    } 
                }

                return 0;
            }
        }
    }
}
