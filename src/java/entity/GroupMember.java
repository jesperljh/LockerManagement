package entity;
/**
 * GroupMember class is created for the sake of organization and to assist with comparing the group member by email, and then mac address.
 * Group has attributes email and macAddress
 * @author Kenneth
 */
public class GroupMember implements Comparable<GroupMember> {

    String email;
    String macAddress;

    /**
     * Creates a new GroupMember object 
     * @param emailValue Email of GroupMember
     * @param macAddressValue time spent in seconds at that place
     */
    public GroupMember(String emailValue, String macAddressValue) {
        this.email = emailValue;
        this.macAddress = macAddressValue;
    }

    /**
     * Returns the email of Group Member
     * @return returns email of Group Member
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email of Group Member
     * @param email Email of Group Member
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Returns the mac address of Group Member
     * @return returns mac address of Group Member
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the mac address of Group Member
     * @param macAddress Mac address of group member
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    /**
     * This method is to sort groups members by email, and mac address
     * @return position -1 if this grouplocation is to be given greater precedence over the comparing group location, 1 if otherwise and 0 if deemed to be equal
     */
    public int compareTo(GroupMember o) {
        
        //Retrieve current and comparing group member's email
        String currentEmail = this.getEmail();
        String comparingEmail = o.getEmail();

        //Compare email without case
        if (currentEmail.compareToIgnoreCase(comparingEmail) > 0) {
            return 1;
        } else if (currentEmail.compareToIgnoreCase(comparingEmail) < 0) {
            return -1;
        } else {
            
            //If email are the same, then no choice, we have to compare the mac addresses
            
            //Retrieve the mac address of current and comparing group members email.
            String currentMacAddress = this.getMacAddress();
            String comparingMacAddress = this.getMacAddress();
            
            //Compare mac addresses :)
            if (currentMacAddress.compareToIgnoreCase(comparingMacAddress) > 0) {
                return 1;
            } else if (currentMacAddress.compareToIgnoreCase(comparingMacAddress) < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
