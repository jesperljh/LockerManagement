package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Deals with the frequent validation of datetime.
 * @author Eugene
 * @version 1.0
 */
public class DateValidation {
    
    /**
     * Validate whether dateTime is of valid with the format of "yyyy-MM-dd HH:mm:ss"
     * @param dateTime date time to be validated
     * @return true if valid, false if not
     */
    public static boolean validateDateFormat(String dateTime) {
        //At this stage, the format is assumed to have a T, and it is not empty.
        
        // below regex validates if the dateTime matches the specific format of "XXXX-XX-XXTXX:XX:XX"
        String regex = "\\b[\\d]{4}-[\\d]{2}-[\\d]{2}T[\\d]{2}:[\\d]{2}:[\\d]{2}\\b";
        
        // return true if the dateTime is of the regex
        if (!dateTime.matches(regex)) {
            return false;
        }
        //Finally, let's try to parse the datetime to see if the format is valid
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            //Setting lenient to false will ensure SimpleDateFormat checks strictly for the validity of your date (for instance, if you set month to 14, it will throw an exception)
            df.setLenient(false);

            //Try to parse datetime, it will throw exception if invalid date, or date range not valid 
            df.parse(dateTime);
            
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }
}
