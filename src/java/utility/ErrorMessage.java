package utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class deals with retrieving of externalized error messages from
 * errors.properties.
 *
 * @author Jesper
 * @version 1.0
 */
public class ErrorMessage {

    /**
     * Returns the error message based on the error name
     * @param errorName The error name (for instance "invalidName", "invalidToken")
     * @return Returns the error message based on the error name
     */
    public static String getMsg(String errorName) {

        Properties props = new Properties();

        InputStream is = DatabaseConnectionManager.class.getResourceAsStream("/errors.properties");
        try {
            props.load(is);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);

        }

        String errorMsg = props.getProperty(errorName);
        return errorMsg;

    }
}
