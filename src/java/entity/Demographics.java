package entity;

import java.util.ArrayList;


/**
 * Demographics object class that represents a Device
 * User Has macAddresss, name, password, email and gender.
 * 
 * @author Eugene
 */

public class Demographics  {

    String macAddress;
    String name;
    String password;
    String email;
    String gender;
    ArrayList<Location> locations;

    /**
     * Creates a Demographics Object with the specified macAddresss, name,
     * password, email and gender.
     *
     * @param macAddress The mac address of the user (40 characters)
     * @param name The name of the user
     * @param password The password of the user
     * @param email The email of the user 
     * @param gender  The gender (M or F)
     */
    public Demographics(String macAddress, String name, String password, String email, String gender) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
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
    public String getEmail() {
        return email;
    }

    /**
     * Sets email of this User
     *
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
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
     * Gets year of this User
     *
     * @return year the year (2010 - 2014)
     */
    public int getYear() {
        if (email.indexOf("2010") != -1) {
            return 2010;
        } else if (email.indexOf("2011") != -1) {
            return 2011;
        } else if (email.indexOf("2012") != -1) {
            return 2012;
        } else if (email.indexOf("2013") != -1) {
            return 2013;
        } else if (email.indexOf("2014") != -1) {
            return 2014;
        }
        return 0;
    }

    /**
     * Gets school of this User
     *
     * @return school the school (business/accountancy/sis/economics/law/socsci)
     */
    public String getSchool() {
        if (email.indexOf("business.smu.edu.sg") != -1) {
            return "business";
        } else if (email.indexOf("accountancy.smu.edu.sg") != -1) {
            return "accountancy";
        } else if (email.indexOf("sis.smu.edu.sg") != -1) {
            return "sis";
        } else if (email.indexOf("economics.smu.edu.sg") != -1) {
            return "economics";
        } else if (email.indexOf("socsc.smu.edu.sg") != -1) {
            return "socsc";
        } else if (email.indexOf("law.smu.edu.sg") != -1) {
            return "law";
        } else {
            return null;
        }
    }

    /**
     * Gets locations of where this Demographics user has been (not used except
     * in Automatic Group Detection)
     *
     * @return ArrayList<Location> of locations where the user has been
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }

    /**
     * Sets a location list of users who have been here before.
     *
     * @param locations the ArrayList<Location> of locations into the
     * Demographics object
     */
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

}
