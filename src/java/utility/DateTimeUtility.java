package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * DateTimeUtility provides methods for the comparison and conversion of timestamps between query intervals.
 * @author Eugene
 */
public class DateTimeUtility {

    /**
     * Returns either dateTime1+9 mins or dateTime2 (depending on which is earlier)
     * @param dateTime1 First date time of comparison
     * @param dateTime2 Second date time of comparison
     * @return Returns either dateTime1+9 mins or dateTime2 (depending on which is earlier) 
     */
    public static String getSecondTimestamp(String dateTime1, String dateTime2) {
        String toReturn = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d2 = sdf.parse(dateTime2);
            Date d1 = sdf.parse(dateTime1);
            //compute time difference in milliseconds between d1 and d2
            long diff = d2.getTime() - d1.getTime();
            //convert time difference from milliseconds to minutes
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);

            long threshold = 9 * 60;
            // this is to check if the diffInMinutes exceeds 9 minutes, if it exceeds we will limit time difference to 9 minutes, else return as it is
            if (diffInSeconds > threshold) {
                //that means secondTimestamp is 9mins from firstTimestamp
                Calendar cal = Calendar.getInstance();
                cal.setTime(d1);
                Integer amount = (int) threshold;
                cal.add(Calendar.SECOND, amount);
                d2 = cal.getTime();
            }
            toReturn = sdf.format(d2);

        } catch (ParseException Ex) {
        }
        //return default secondTimestamp or new secondTimestamp if its more than 9mins from firstTimestamp
        return toReturn;
    }

    /**
     * Compute time difference between first dateTime and second dateTime
     * @param dateTime1 First date time of comparison
     * @param dateTime2 Second date time of comparison
     * @return number of seconds between first and second date time
     */
    public static int getTimeDiffInSeconds(String dateTime1, String dateTime2) {
        long diffInSeconds = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d2 = sdf.parse(dateTime2);
            Date d1 = sdf.parse(dateTime1);
            //compute time difference in milliseconds between d1 and d2
            long diff = d2.getTime() - d1.getTime();
            //convert time difference from milliseconds to seconds
            diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        } catch (ParseException Ex) {
        }
        //to return actual time difference between 2 timestamp or 540seconds(9mins) if actual time difference is more than 9mins
        return (int) diffInSeconds;
    }

    /**
     * Returns dateTime with specified minutes subtracted from currentTime
     * @param currentTime Current time stamp
     * @param minutes Number of minutes to subtract from currentTime 
     * @return dateTime with specified minutes subtracted from currentTime
     */
    public static String getTimeBefore(String currentTime, int minutes) {
        String toReturn = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            //minus k minutes from current dateTime
            cal.add(Calendar.MINUTE, -minutes);
            Date date2 = cal.getTime();
            //convert date in date format to string format to be returned
            toReturn = sdf.format(date2);
        } catch (ParseException ex) {
        }
        return toReturn;

    }

    /**
     * Returns dateTime with specified minutes added to currentTime
     * @param currentTime Current time stamp
     * @param minutes Number of minutes to add to currentTime 
     * @return dateTime with specified minutes added to currentTime
     */
    public static String getTimeAfter(String currentTime, int minutes) {
        String toReturn = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            //add k minutes from current dateTime
            cal.add(Calendar.MINUTE, minutes);
            Date date2 = cal.getTime();
            //convert date in date format to string format to be returned
            toReturn = sdf.format(date2);
        } catch (ParseException ex) {
        }
        return toReturn;

    }

    /**
     * Compare 2 Dates and return whether dateTime1 is before, equal or after
     * dateTime2
     *
     * @param dateTime1 First datetime to be compared
     * @param dateTime2 Seocnd date time to be compared
     * @return -1 if dateTime1 is before dateTime2, 0 if dateTime1 equals dateTime2, 1 if dateTime1 is after dateTime2
     */
    public static int compareDates(String dateTime1, String dateTime2) {
        int status = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = sdf.parse(dateTime1);
            Date date2 = sdf.parse(dateTime2);
            if (date1.before(date2)) {
                status = -1;
            }
            if (date1.after(date2)) {
                status = 1;
            }
        } catch (ParseException ex) {
        }
        return status;
    }


}
