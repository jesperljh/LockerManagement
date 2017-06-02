package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utility.DatabaseConnectionManager;

/**
 * TableManager is the data access object which contains methods to drop and create tables
 * 
 * @author Jiacheng/Jing Xiang 
 */
public class TableManager {

    /**
     * Connection object is required to access the database.
     */
    private Connection conn;
    /**
     * PreparedStatement is a parameterized SQL statement which is used to query
     * the database.
     */
    private PreparedStatement pstmt;

    /**
     * <br />Drop all tables in database - demographics, location and locationlookup.
     */
    public void dropAllTables() {
        
        String dropLocationSQL = "DROP TABLE if exists location;";
        String dropLocationlookupSQL = "DROP TABLE if exists locationlookup;";
        String dropDemographicsSQL = "DROP TABLE if exists demographics;";
        try {
            conn = DatabaseConnectionManager.getConnection();
            pstmt = conn.prepareStatement(dropDemographicsSQL);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(dropLocationSQL);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(dropLocationlookupSQL);
            pstmt.executeUpdate();
            if (pstmt != null) {
                pstmt.close();
                System.out.println("*** PreparedStatement closed");
            }
        } catch (SQLException e) {
            System.out.println("Failed to prepare statement");
        } finally {
            DatabaseConnectionManager.closeConnection(conn);
        }
    }

    /**
     * <br />Create all tables in database - demographics, location and
     * locationlookup.
     */
    public void createAllTables() {
        
     String createDemographicsSQL = "create table if not exists demographics\n"
                + "(\n"
                + "mac_address char(40),\n"
                + "student_name varchar(255),\n"
                + "student_password varchar(255),\n"
                + "email varchar(255) not null,\n"
                + "gender char(1),\n"
                + "CONSTRAINT demographics_pk PRIMARY KEY (email)\n"
                //+ "CONSTRAINT demographics_fk FOREIGN KEY (mac_address) REFERENCES Location (mac_address)\n"
                + ");";
        /*String createLocationSQL = "create table if not exists location\n"
                + "(\n"
                + "time_stamp DATETIME not null,\n"
                + "mac_address char(40) not null,\n"
                + "location_id int (12) not null,\n"
                + "CONSTRAINT location_pk PRIMARY KEY (time_stamp, mac_address, location_id),\n"
                + "CONSTRAINT location_fk FOREIGN KEY (location_id) REFERENCES locationlookup (location_id)\n"
                + ");";*/
        String createLocationSQL = "CREATE TABLE  if not exists location (\n" 
                +"  `rowId` int(11) NOT NULL,\n" 
                +"  `time_stamp` datetime NOT NULL,\n" 
                +"  `mac_address` char(40) NOT NULL,\n" 
                +"  `location_id` int(12) NOT NULL,\n" 
                +"  KEY `time_stamp` (`time_stamp`,`mac_address`,`location_id`)\n" 
                +") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        String createLocationLookupSQL = "create table if not exists locationlookup\n"
                + "(\n"
                + "location_id int(12) not null,\n"
                + "semantic_place varchar(255),\n"
                + "CONSTRAINT locationlookup_pk PRIMARY KEY (location_id)\n"
                + ");";
        
        try {
            conn = DatabaseConnectionManager.getConnection();
            pstmt = conn.prepareStatement(createLocationLookupSQL);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(createLocationSQL);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(createDemographicsSQL);
            pstmt.executeUpdate();
            if (pstmt != null) {
                pstmt.close();
                System.out.println("*** PreparedStatement closed");
            }

        } catch (SQLException e) {
            System.out.println("Failed to prepare statement" + e);
        } finally {
            DatabaseConnectionManager.closeConnection(conn);
        }
    }
}
