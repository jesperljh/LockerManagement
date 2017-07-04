package entity;

import java.util.ArrayList;


/**
 * Demographics object class that represents a Device
 * User Has macAddresss, name, password, email and gender.
 * 
 * @author Jesper
 */

public class Demographics  {

    String macAddress;
    String name;
    String password;
    String sid;
    String gender;
    String role;
    String neighbourhood;

    /**
     * Creates a Demographics Object with the specified macAddresss, name,
     * password, email and gender.
     *
     * @param macAddress The mac address of the user (40 characters)
     * @param name The name of the user
     * @param password The password of the user
     * @param sid The sid of the user 
     * @param gender  The gender (M or F)
     * @param role The role (admin/manager/user)
     */
    public Demographics(String macAddress, String name, String password, String sid, String gender, String role, String neighbourhood) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.sid = sid;
        this.gender = gender;
        this.role = role;
        this.neighbourhood = neighbourhood;
    }
    

    /**
     * Gets the macAddress of this User
     *
     * @return the macAddress of this User
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets macAddress of this User
     *
     * @param macAddress the macAddresss of the user
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Gets the name of this User
     *
     * @return the name of this User
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of this User
     *
     * @param name the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password of this User
     *
     * @return the password of this User
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password of this User
     *
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email of this User
     *
     * @return the email of this User
     */
    public String getSid() {
        return sid;
    }

    /**
     * Sets email of this User
     *
     * @param email the email of the user
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * Gets the gender of this User
     *
     * @return the gender of this User
     */
    public String getGender() {
        return gender;
    }  
    
    /**
     * Sets gender of this User
     *
     * @param gender the gender (M/F)
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * Gets the role of this User
     *
     * @return the role of this User
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets role of this User
     *
     * @param role the role of the user (admin/manager/user)
     */
    public void setRole(String role) {
        this.role = role;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
}
