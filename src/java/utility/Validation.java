package utility;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class deals with the validation of various fields
 * @author Jesper
 */
public class Validation {



    /**
     * Validate whether the timestamp has a valid date and time (this is usually
     * from the CSV file, and has no T)
     * @param timeStamp the timestamp to be validated
     * @return true if valid, false if invalid
     */
    public static boolean validateTimeStamp(String timeStamp) {
        // below regex validates if the dateTime matches the specific format of "XXXX-XX-XXTXX:XX:XX"
        String regex = "\\b[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}\\b";
        
        // return true if the timeStamp is of the regex format
        if (!timeStamp.matches(regex)) {
            return false;
        }
        
        //let's try to parse the datetime to see if the format is valid
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Setting lenient to true will ensure SimpleDateFormat checks the validity of your date (for instance, if you set month to 14, it will throw an exception)
            sdf.setLenient(false);

            //Try to parse datetime, it will throw exception if invalid 
            sdf.parse(timeStamp);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Validate whether the Mac Address is an exact 40-letters hexadecimal
     *
     * @param macAddress the mac address to be validated
     * @return true if valid, false if invalid
     */
    public static boolean validateMacAddress(String macAddress) {

        // using regex to simulate the criteria
        String regex = "\\b[a-fA-F\\d]{40}\\b";
        return (macAddress.matches(regex));
    }

    /**
     * Validate whether the password is lesser than 8-letters and does not
     * include in-between white spaces
     *
     * @param password the password to be validated
     * @return true if valid, false if invalid
     */
    public static boolean validatePassword(String password) {
        if (password.length() < 8 || password.indexOf(" ") != -1) {
            return false;
        }
        return true;
    }

    /**
     * Validate whether the email matches the specification requirements
     *
     * @param email the email to be validated
     * @return true if valid, false if invalid
     */
    public static boolean validateEmail(String email) {

        String regex = "\\b[a-zA-Z\\d.]+.201[0-4]@(business|accountancy|socsc|law|economics|sis).smu.edu.sg\\b";
        return (email.matches(regex));
    }

    /**
     * Validate whether the gender is 'M' or 'F' case-insensitive
     *
     * @param gender the gender to be validated
     * @return true if valid, false if invalid
     */
    public static boolean validateGender(String gender) {
        // changing it to all upper case since it is case insensitive
        gender = gender.toUpperCase();
        if (gender.equals("F") || gender.equals("M")) {
            return true;
        } else {
            return false;
        }
    }
}
