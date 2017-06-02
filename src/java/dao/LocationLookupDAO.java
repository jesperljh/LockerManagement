package dao;

import entity.LocationLookup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import utility.DatabaseConnectionManager;

/**
 * LocationLookupDAO is the data access object which contains methods needed to
 * access data from locationlookup table in database
 *
 * @author Kenneth / Eugene
 */
public class LocationLookupDAO {

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
     * ResultSet is a table of data representing a database result set.
     */
    private ResultSet rs;

    /**
     * <br />Retrieves an ArrayList of LocationLookup for the Heatmap functionality
     *
     * @param floor a particular floor of SIS building
     * @param dateTime 15 mins before the specified dateTime
     * @param endDateTime the specified dateTime
     * @return ArrayList<LocationLookup> or null if no results
     */
    public LinkedHashMap<String, Object> getHeatmap(int floor, String dateTime, String endDateTime) {

        LinkedHashMap<String, Object> heatmapResults = new LinkedHashMap<String, Object>();

        //Set the floorSearchField based on the floor (business logic is specified in wiki)
        String floorSearchField = "";
        switch (floor) {
            case 0:
                floorSearchField = "SMUSISB1";
                break;
            case 1:
                floorSearchField = "SMUSISL1";
                break;
            case 2:
                floorSearchField = "SMUSISL2";
                break;
            case 3:
                floorSearchField = "SMUSISL3";
                break;
            case 4:
                floorSearchField = "SMUSISL4";
                break;
            case 5:
                floorSearchField = "SMUSISL5";
        }

        //Prepare SQL statement
        String stmt = "SELECT * FROM locationlookup WHERE semantic_place LIKE ? ORDER by semantic_place ASC";
        try {
            //Retrieves connection
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Adds a wildcard to search field
            pstmt.setString(1, floorSearchField + "%");

            //Store resultset
            rs = pstmt.executeQuery();

            //Run through the Result Set (data)
            while (rs.next()) {

                //Store location id from first position
                int locationIdR = rs.getInt(1);

                //Store semantic place from second position
                String semanticPlaceR = rs.getString(2);

                //Initialize all semantic place
                heatmapResults.put(semanticPlaceR, 0);

            }

            // Prepare SQL statement
            stmt = "SELECT * FROM location WHERE time_stamp > ? and time_stamp <= ? GROUP by mac_address";

            //ArrayList of mac_addresses
            ArrayList<String> macAddressList = new ArrayList<String>();
            //Prepare the second SQL statement
            PreparedStatement secondPstmt = conn.prepareStatement(stmt);

            // Set the parameters into second PreparedStatement
            secondPstmt.setString(1, dateTime);
            secondPstmt.setString(2, endDateTime);

            //Declare secondResultset
            ResultSet secondRs = secondPstmt.executeQuery();

            //Get results set
            secondRs = secondPstmt.executeQuery();

            //Run through the second data set which contains the location (linked to locationlookup)
            while (secondRs.next()) {
                // Retrieve field from Resultset and add to macAddressList
                macAddressList.add(secondRs.getString(3));
            }

            for (Map.Entry<String, Object> entry : heatmapResults.entrySet()) {
                //System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            //Now only retrieve latest updates
            for (String macAddressQueryString : macAddressList) {

                // Prepare SQL statement
                stmt = "SELECT ll.semantic_place FROM location `l`, locationlookup `ll` WHERE l.location_id=ll.location_id AND l.time_stamp > ? and l.time_stamp <= ? AND l.mac_address = ? ORDER by l.time_stamp DESC LIMIT 1";
                System.out.println(stmt);

                // if second PreparedStatement is not null, close it
                if (secondPstmt != null) {
                    secondPstmt.close();
                }

                // Prepare statement
                secondPstmt = conn.prepareStatement(stmt);

                // Set parameters into preparedStatement
                secondPstmt.setString(1, dateTime);
                secondPstmt.setString(2, endDateTime);
                secondPstmt.setString(3, macAddressQueryString);

                //Get results set
                secondRs = secondPstmt.executeQuery();

                //Run through the second data set which contains the location (linked to locationlookup)
                if (secondRs.next()) {
                    String semanticPlace = secondRs.getString(1);
                    if (heatmapResults.get(semanticPlace) != null) {
                        int crowdNumber = (Integer) heatmapResults.get(semanticPlace);

                        //Add current crowd number + current location crowd size
                        crowdNumber++;

                        //Add back into LinkedHashMap
                        heatmapResults.put(semanticPlace, crowdNumber);
                    }
                }
            }

            // Close ResultSet if not null
            if (rs != null) {
                rs.close();
            }

            // Close PreparedStatement if not null
            if (pstmt != null) {
                pstmt.close();
            }

            // Close second ResultSet if not null
            if (secondRs != null) {
                secondRs.close();
            }
            // Close second PreparedStatement if not null
            if (secondPstmt != null) {
                secondPstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Failed to prepare statement for getHeatmap (first statement):" + e);
        } finally {
            //Close connection from DatabaseConnectionManager after usedpool
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Finally return all the location lookups, or null if no matches.
        return heatmapResults;
    }

    /**
     * <br />Retrieves a LocationLookup based on Semantic Place
     *
     * @param semanticPlace the semantic place to be queried
     *
     * @return an ArrayList of LocationLookup or null if no result
     */
    public LocationLookup retrieve(String semanticPlace) {

        //Declare and instantiate LocationLookup object
        LocationLookup locationLookup = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM locationlookup WHERE semantic_place = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters
            pstmt.setString(1, semanticPlace);

            //Execute SQL statement
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) {

                //Set results into variables                
                int locationIdR = Integer.parseInt(rs.getString(1));
                String semanticPlaceR = rs.getString(2);

                //Instantiate object with results
                locationLookup = new LocationLookup(locationIdR, semanticPlaceR);
            }
            //If result set is not null, close it
            if (rs != null) {
                rs.close();
            }
            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occured with retrieve:" + e);
        } finally {
            // Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }

        //Returns LocationLookup object or null
        return locationLookup;
    }

    /**
     * <br />Retrieves a LocationLookup based on locationId
     *
     * @param locationId specified locationId that is to be queried
     *
     * @return LocationLookup or null if no result
     */
    public LocationLookup retrieve(int locationId) {

        //Declare and instantiate LocationLookup object
        LocationLookup locationLookup = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM locationlookup WHERE location_id = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters
            pstmt.setInt(1, locationId);

            //Execute SQL statement
            rs = pstmt.executeQuery();

            //If there is results
            if (rs.next()) {

                //Set results into variables                
                int locationIdR = Integer.parseInt(rs.getString(1));
                String semanticPlaceR = rs.getString(2);

                //Instantiate object with results
                locationLookup = new LocationLookup(locationIdR, semanticPlaceR);
            }
            //If result set is not null, close it
            if (rs != null) {
                rs.close();
            }
            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occured with retrieve:" + e);
        } finally {
            // Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }

        //Returns LocationLookup object or null
        return locationLookup;
    }

    /**
     * <br />Retrieves a LocationLookup based on locationId
     *
     * @param conn connection to be used
     * @param locationId the locationId to be queried
     * @return LocationLookup or null if no result
     */
    public LocationLookup retrieve(Connection conn, int locationId) {

        //Declare and instantiate LocationLookup object
        LocationLookup locationLookup = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM locationlookup WHERE location_id = ?";
        try {

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters
            pstmt.setInt(1, locationId);

            //Execute SQL statement
            rs = pstmt.executeQuery();

            //If there is results
            if (rs.next()) {

                //Set results into variables                
                int locationIdR = Integer.parseInt(rs.getString(1));
                String semanticPlaceR = rs.getString(2);

                //Instantiate object with results
                locationLookup = new LocationLookup(locationIdR, semanticPlaceR);
            }
            //If result set is not null, close it
            if (rs != null) {
                rs.close();
            }
            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occured with retrieve:" + e);
        }
        //Returns LocationLookup object or null
        return locationLookup;
    }

    /**
     * <br />Retrieves all unique LocationLookup from the locationlookup table in
     * database
     *
     * @return ArrayList<LocationLookup> object which contains all
     * LocationLookup objects in database
     */
    public ArrayList<LocationLookup> retrieveAll() {

        //Declare and instantiate ArrayList of LocationLookup to store LocationLookup objects
        ArrayList<LocationLookup> result = new ArrayList<LocationLookup>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM locationlookup GROUP BY semantic_place";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Execute SQL statement
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) {

                //Set results into variables 
                int locationIdR = Integer.parseInt(rs.getString(1));
                String semanticPlaceR = rs.getString(2);

                //Instantiate object with results
                LocationLookup locationLookup = new LocationLookup(locationIdR, semanticPlaceR);
                result.add(locationLookup);
            }
            //If result set is not null, close it
            if (rs != null) {
                rs.close();
            }
            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues 
            System.out.println("Error occured with retrieve:" + e);
        } finally {
            // Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }

        //Returns LocationLookup object or null
        return result;
    }

    /**
     * <br />Creates a Location record based on specified locationId and semanticPlace
     *
     * @param conn the shared connection to be used
     * @param locationId locationId to be inserted
     * @param semanticPlace semantic-place to be inserted
     * @return true if successfully added, false if failed
     */
    public boolean create(Connection conn, int locationId, String semanticPlace) {

        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare statement
        String stmt = "INSERT into locationlookup VALUES (?,?)";

        try {
            //Prepare connection
            pstmt = conn.prepareStatement(stmt);

            //Pass parameters into prepared statement
            pstmt.setString(1, Integer.toString(locationId));
            pstmt.setString(2, semanticPlace);

            //Execute query
            pstmt.executeUpdate();

            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Sets status to false, since there is an error
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues             
            System.out.println("Error occurred with create:" + e.getMessage());
        }
        //Returns true if successful add, returns false if fail
        return status;
    }

    /**
     * <br />Update a Location record
     *
     * @param locationId Location ID (used to identify the Location)
     * @param semanticPlace Semantic Place
     *
     * @return true if successfully updated, false if otherwise
     */
    public boolean update(int locationId, String semanticPlace) {

        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "UPDATE locationLookup "
                + "SET semantic_place=?"
                + "WHERE location_id = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Pass parameters into prepared statement
            pstmt.setString(1, semanticPlace);
            pstmt.setString(2, Integer.toString(locationId));

            //Execute update
            pstmt.executeUpdate();

            //Close prepared statement
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Sets status to false, since there is an error            
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {

            //Close connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns status of update (true if successful, false if unnecessful)
        return status;
    }

    /**
     * <br />Delete a Location record base on specified locationId
     *
     * @param locationId Location ID (used to identify the Location)
     *
     * @return true if successfully updated, false if otherwise
     */
    public boolean delete(int locationId) {

        //Assume status is true, set false only if exception is caught
        boolean status = true;
        
        // prepared SQL statement
        String stmt = "DELETE from locationLookup WHERE location_id = ?";
        try {
            // get Connetion from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();
            
            // prepared statement
            pstmt = conn.prepareStatement(stmt);
            
            // set parameters for preparedstatement
            pstmt.setString(1, Integer.toString(locationId));
            
            // execute update
            pstmt.executeUpdate();
            
            // close preparedstatement if not null
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            status = false;
            System.out.println("Error occurred with delete:" + e);
        } finally {
            // Close connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }

        return status;
    }
}
