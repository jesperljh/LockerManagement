package entity;

/**
 * Location object class that represents a location presence 
 * @author Eugene 
 * Location has timestamp, macAddress, locationId.
 */
public class Location {

    String timestamp;
    String macAddress;
    int locationId;

    /**
     * Creates a Location Object with the specified timestamp, macAddress,
     * locationId
     *
     * @param timestamp timestamp of the location object
     * @param macAddress mac address of the location object
     * @param locationId location id of the location object
     */
    public Location(String timestamp, String macAddress, int locationId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.locationId = locationId;
    }

    /**
     * Gets the timestamp of this User
     *
     * @return the timestamp of this User
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp of this Location
     *
     * @param timestamp of this Location
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the macAddress of this Location
     *
     * @return the macAddress of this Location
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets macAddress of this Location
     *
     * @param macAddress of this Location
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Gets the locationId of this Location
     *
     * @return the locationId of this Location
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Sets locationId of this Location
     *
     * @param locationId of this Location
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

}
