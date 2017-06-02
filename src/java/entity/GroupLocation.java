package entity;

/**
 * GroupLocation class (created for the sake of organization and to assist with comparing the grouplocation by location id and time spent)
 * Group has an ArrayList of GroupMember, ArrayList of GroupLocation, and a LinkedHashMap of Location Records
 * @author Kenneth
 */
public class GroupLocation implements Comparable<GroupLocation> {

    public String locationId;
    public int timeSpent;
    /**
     * Creates a new GroupLocation object 
     * @param locationId location id of the place
     * @param timeSpent time spent in seconds at that place
     */
    public GroupLocation(String locationId, int timeSpent) {
        this.locationId = locationId;
        this.timeSpent = timeSpent;
    }


    /**
     * Returns the location Id
     * @return returns locationId
     */
    public String getLocationId() {
        return locationId;
    }


    /**
     * Sets the location Id
     * @param Sets locationId
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * Returns the time spent at this location id
     * @return returns the time spent at this location id
     */
    public int getTimeSpent() {
        return timeSpent;
    }
    /**
     * Sets the time spent at this location id
     * @param Sets time spent at this location id
     */

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }


    /**
     * This method is to sort groups location by location id, and time spent
     * @return position -1 if this grouplocation is to be given greater precedence over the comparing group location, 1 if otherwise and 0 if deemed to be equal
     */
    public int compareTo(GroupLocation o) {

        //Get the current and comparing location ids
        int currentLocationId = Integer.parseInt(this.getLocationId());
        int comparingLocationId = Integer.parseInt(o.getLocationId());

        //The current location id is greater than the comparing location id
        if (currentLocationId < comparingLocationId) {
            return -1;
        } else if (currentLocationId > comparingLocationId) {
            //The current location id is greater than the comparing location id
            return 1;
        } else {
            //Return equal
            return 0;
        }
    }
}
