package entity;

import java.util.ArrayList;
/**
 * LocationLookup object class that represents a unique location
 * LocationLookup has locationId, semanticPlace, locationList.
 * @author Kenneth
 */
public class LocationLookup {

    int locationId;
    String semanticPlace;
    ArrayList<Location> locationList;

    /**
     * Creates a LocationLookup Object with the specified locationId,
     * semanticPlace, locationList.
     *
     * @param locationId location id of the semantic place
     * @param semanticPlace the name of the place
     */
    public LocationLookup(int locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;

    }

    /**
     * Gets the locationId of this location
     *
     * @return the locationId of this location
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

    /**
     * Gets the semanticPlace of this location
     *
     * @return the semanticPlace of this location
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }

    /**
     * Sets semanticPlace of this Location
     *
     * @param semanticPlace of this Location
     */
    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    /**
     * Gets the list of location
     *
     * @return the location list
     */
    public ArrayList<Location> getLocationList() {
        return locationList;
    }

    /**
     * Sets locationLookups of this Location
     *
     * @param locationLookups of this Location
     */
    public void setLocationLookups(ArrayList<Location> locationList) {
        this.locationList = locationList;
    }

}
