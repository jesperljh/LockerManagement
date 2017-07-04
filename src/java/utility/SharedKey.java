package utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SharedKey retrieves the key from settings.properties file
 * @author Jesper
 * @version 1.0
 */
public class SharedKey {
    /**
     * This method returns the string from the settings.properties file.
     * @return the shared key for signing token validation 
     */
    public static String getKey() {
        // Instantiate a new Properties object
        Properties props = new Properties();

        // Get inputstream from props file
        InputStream is = DatabaseConnectionManager.class.getResourceAsStream("/settings.properties");
        try {
            // Load connection.properties into props
            props.load(is);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sharedKey = props.getProperty("sharedKey");
        return sharedKey;
    }
}
