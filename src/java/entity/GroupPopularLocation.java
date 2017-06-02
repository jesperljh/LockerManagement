package entity;

/**
 * GroupPopularLocation class is created for the sake of organization and to
 * assist with comparing the group for Top K Popular Places by Group
 * GroupPopularLocation has attributes such as semanticPlace,numberOfGroups and
 * rank
 *
 * @author Kenneth
 */
public class GroupPopularLocation implements Comparable<GroupPopularLocation> {

    String semanticPlace;
    int numberOfGroups;
    int rank;

    /**
     * Creates a GroupPopularLocation object
     *
     * @param semanticPlace The name of the place
     * @param numberOfGroups The number of groups
     */
    public GroupPopularLocation(String semanticPlace, int numberOfGroups) {
        this.semanticPlace = semanticPlace;
        this.numberOfGroups = numberOfGroups;
    }

    /**
     * Returns the rank of the PopularLocation
     *
     * @return rank Rank of the location from 1 to 10
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets the rank of the PopularLocation
     *
     * @param rank Sets the Rank of the location from 1 to 10
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns the name of the semantic place
     *
     * @return the name of the semantic place
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }

    /**
     * Sets the name of the semantic place
     *
     * @param semanticPlace Sets the name of the semantic place
     */
    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    /**
     * Returns the number of groups with the PopularLocation
     *
     * @return returns the number of groups within the PopularLocation
     */
    public int getNumberOfGroups() {
        return numberOfGroups;
    }

    /**
     * Sets the number of groups in this popular location
     * @param numberOfGroups Sets the number of groups in this popular location
     */
    public void setNumberOfGroups(int numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    /**
     * Returns the position of the GroupPopularLocation based on the number of groups they have
     * @param o The comparing group to be matched against
     * @return position -1 if the current popularlocation has more groups, 1 if they have less, and 0 if the same
     */
    public int compareTo(GroupPopularLocation o) {
        int currentGroupSize = this.numberOfGroups;
        int comparingGroupSize = o.getNumberOfGroups();
        if (currentGroupSize > comparingGroupSize) {
            return -1;
        } else if (currentGroupSize < comparingGroupSize) {
            return 1;
        } else {
            return 0;
        }
    }
}
