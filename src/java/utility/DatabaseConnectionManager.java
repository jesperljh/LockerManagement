package utility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseConnectionManager is used to contain methods to retrieve and close
 * connection to database
 *
 * @author Jiacheng / Jing Xiang
 * @version 1.0
 */
public class DatabaseConnectionManager {

    /**
     * This contains the reference to the Properties file
     */
    private static final String PROPS_FILE = "/settings.properties";
    /**
     * This contains the reference to the MySQL driver class
     */
    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
    /**
     * Contains the username for database
     */
    private static String dbUser;
    /**
     * Contains the password for database
     */
    private static String dbPass;
    /**
     * Contains the name of database
     */
    private static String dbName;
    /**
     * Contains the url of database
     */
    private static String dbUrl;
    /**
     * Contains the host of database
     */
    private static String dbHost;
    /**
     * Contains the port of database
     */
    private static String dbPort;

    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Instantiate a new Properties object
        Properties props = new Properties();

        // Get inputstream from props file
        InputStream is = DatabaseConnectionManager.class.getResourceAsStream(PROPS_FILE);
        try {
            // Load connection.properties into props
            props.load(is);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Get db connection settings from properties 
        dbHost = props.getProperty("jdbc.dbHost");
        dbPort = props.getProperty("jdbc.dbPort");
        dbName = props.getProperty("jdbc.dbName");
        dbUser = props.getProperty("jdbc.userName");
        dbPass = props.getProperty("jdbc.userPassword");
        dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");

        // if host is not null, it means that 
        if (host != null) {
            System.out.println("*** Openshift Database Settings loaded ***");
            dbHost = host;
            dbPort = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
            dbName = System.getenv("OPENSHIFT_APP_NAME");
            dbUser = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
            dbPass = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
            dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.out.println("*** DATABASE Connection Error from DatabaseConnectionManager.java ***");
            System.out.println(e.getMessage());
        }

    }

    /**
     * Get connection from DriverManager based on the dbUrl, dbUser and dbPass
     *
     * @return Database Connection Object for using
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    /**
     * Close the specified connection
     *
     * @param conn the Connection Object to be closed
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                // System.out.println("*** Connection closed ***");
            }
        } catch (SQLException e) {
            //System.out.println("Fail to close");
        }
    }
}
