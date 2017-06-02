package dao;

import entity.Location;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.DatabaseConnectionManager;

/**
 * LocationDAO is the data access object which contains methods needed to access
 * data from location table in database
 *
 * @author Kenneth / Eugene
 */
public class LocationDAO {

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
     * <br />Retrieves one matching Location record based on timeStamp,
     * macAddress,locationId
     *
     * @param timeStamp the location's timestamp
     * @param macAddress the location's mac-address
     * @param locationId the location's location id
     *
     * @return the matching Location object, null if absent
     */
    public Location retrieve(String timeStamp, String macAddress, int locationId) {
        //Initialize Location object as null
        Location location = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM location WHERE time_stamp = ? AND mac_address = ? AND location_id = ?";

        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, timeStamp);
            pstmt.setString(2, macAddress);
            pstmt.setInt(3, locationId);

            //Execute query (retrieve)
            rs = pstmt.executeQuery();

            //If there is a result
            if (rs.next()) {

                //Set record results into variable
                String timeStampR = rs.getString(2);
                String macAddressR = rs.getString(3);
                int locationIdR = rs.getInt(4);

                //Initiate Location object based on results from the database
                location = new Location(timeStampR, macAddressR, locationIdR);
            }

            //If result set is not null, close it
            if (rs != null) {
                rs.close();
            }
            //If prepared statement is not null, close it.
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to retrieve location:" + e);
        } finally {
            //Close the connection 
            DatabaseConnectionManager.closeConnection(conn);
        }
        return location;
    }

    /**
     * <br />Creates a Location record in the database
     *
     * @param timeStamp the location's timestamp
     * @param macAddress the location's mac-address
     * @param locationId the location's location id
     *
     * @return true if creation is successful or false if failed
     */
    public boolean create(String timeStamp, String macAddress, int locationId) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "INSERT into location VALUES (?,?,?)";

        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, timeStamp);
            pstmt.setString(2, macAddress);
            pstmt.setInt(3, locationId);

            //Execute update (create)
            pstmt.executeUpdate();

            //If prepared statement is not null
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false since an exception has occured
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Error occurred with create:" + e);
        } finally {
            //Close the connection 
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successful, false if failed
        return status;
    }

    /**
     * <br />Deletes a Location record in the database
     *
     * @param timeStamp the location's timestamp
     * @param macAddress the location's mac-address
     * @param locationId id the location's location id
     *
     * @return true if successful or false if failed
     */
    public boolean delete(String timeStamp, String macAddress, int locationId) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "DELETE from demographics WHERE time_stamp = ? AND mac_address = ? AND location_id = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, timeStamp);
            pstmt.setString(2, macAddress);
            pstmt.setInt(3, locationId);

            //Execute update (delete)
            pstmt.executeUpdate();

            //If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Set status to false, since there is an error
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with delete:" + e);
        } finally {
            //Close the connection
            DatabaseConnectionManager.closeConnection(conn);
        }
        return status;
    }

    /**
     * <br />Retrieves list of all Location updates from a particular user within user
     * update window
     *
     * @param macAddress the companion macAddress
     * @param startDateTime the specified user update window start dateTime
     * @param endDateTime the specified user update window end dateTime
     *
     * @return an ArrayList<Location> object containing Location objects from a
     * particular user within user update window, return empty arraylist if
     * absent
     */
    public ArrayList<Location> getUserUpdates(String macAddress, String startDateTime, String endDateTime) {

        // instantiate the resultList to contain Location objects belonging to particular user within user update window
        ArrayList<Location> resultList = new ArrayList<Location>();

        // Prepare SQL statement
        String stmt = "SELECT * FROM location WHERE time_stamp > ? AND time_stamp <= ? AND mac_address = ? ORDER BY mac_address, time_stamp ASC;";
        try {
            // get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            // Prepare the statement
            pstmt = conn.prepareStatement(stmt);

            // Set parameters into PreparedStatement
            pstmt.setString(1, startDateTime);
            pstmt.setString(2, endDateTime);
            pstmt.setString(3, macAddress);

            // Execute query (retrieve)
            rs = pstmt.executeQuery();
            while (rs.next()) {

                // retrieve fields from ResultSet
                String timeStampR = rs.getString(2);
                String macAddressR = rs.getString(3);
                int locationIdR = rs.getInt(4);

                // create new Location based on retrieved fields
                Location location = new Location(timeStampR, macAddressR, locationIdR);

                // add new Location to resultList
                resultList.add(location);
            }

            // If resultset is not null, close it
            if (rs != null) {
                rs.close();
            }

            // If preparedstatement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with delete:" + e);
        } finally {
            // Close the connection
            DatabaseConnectionManager.closeConnection(conn);
        }
        return resultList;
    }

    /**
     * <br />Retrieves a list of unique mac-addresses within the specified
     * startDateTime and endDateTime
     *
     * @param startDateTime the specified user update window start dateTime
     * @param endDateTime the specified user update window end dateTime
     *
     * @return ArrayList<String> resultList of the location updates, return
     * empty ArrayList if absent
     */
    public ArrayList<String> getWindowUniqueMacAddresses(String startDateTime, String endDateTime) {

        // instantiate an arrayList of resultList to store list of unique mac-addresses within the specified startDateTime and endDateTime
        ArrayList<String> resultList = new ArrayList<String>();

        // Prepare SQL statement
        String stmt = "SELECT mac_address FROM location WHERE time_stamp > ? AND time_stamp <= ? GROUP BY mac_address;";

        try {
            // Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            // Prepare statement
            pstmt = conn.prepareStatement(stmt);

            // set parameters into preparedstatement
            pstmt.setString(1, startDateTime);
            pstmt.setString(2, endDateTime);

            // execute query (retrieve)
            rs = pstmt.executeQuery();

            while (rs.next()) {

                // retrieve field from ResultSet
                String macAddressR = rs.getString(1);

                // add retrieved field into resultList
                resultList.add(macAddressR);
            }

            // if resultset is not null, close it
            if (rs != null) {
                rs.close();
            }

            // if preparedstatement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with delete:" + e);
        } finally {
            // Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }
        return resultList;
    }

    /**
     * <br />Retrieves list of unique users latest update located at origin between
     * startDateTime and endDateTime
     *
     * @param origin the specified semantic place
     * @param startDateTime the query start date time
     * @param endDateTime the query end date time
     *
     * @return ArrayList of Location object
     */
    public ArrayList<Location> getOriginUserList(String origin, String startDateTime, String endDateTime) {
        //Instantiate new ArrayList<Location> object 
        ArrayList<Location> resultList = new ArrayList<Location>();

        ArrayList<String> macAddressList = new ArrayList<String>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM location WHERE time_stamp > ? and time_stamp <= ? GROUP by mac_address";

        try {
            // Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            // Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            // Set parameters into prepared statement
            pstmt.setString(1, startDateTime);
            pstmt.setString(2, endDateTime);

            // Execute query
            rs = pstmt.executeQuery();

            // for all locations that match the SQL query
            while (rs.next()) {
                // Set record results into variable
                String macAddressR = rs.getString(3);

                // Instantiate new Location object based on each row of the resultset
                macAddressList.add(macAddressR);
            }

            // Close the Preparedstatement if not null
            if (pstmt != null) {
                pstmt.close();
            }

            // Close the ResultSet if not null
            if (rs != null) {
                rs.close();
            }

            // For each macAddress in macAddressList
            for (String macAddress : macAddressList) {

                // Prepare SQL statement
                stmt = "SELECT ll.semantic_place, l.* FROM location `l`, locationlookup `ll` WHERE l.location_id=ll.location_id AND l.time_stamp > ? and l.time_stamp <= ? AND l.mac_address = ? ORDER by l.time_stamp DESC LIMIT 1";

                // Prepare statement
                pstmt = conn.prepareStatement(stmt);

                // Set parameters into prepared statement
                pstmt.setString(1, startDateTime);
                pstmt.setString(2, endDateTime);
                pstmt.setString(3, macAddress);

                // Execute query (retrieve)
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // retrieve fields from ResultSet
                    String semanticPlace = rs.getString(1);
                    String timeStamp = rs.getString(3);
                    String macAdd = rs.getString(4);
                    int locationId = Integer.parseInt(rs.getString(5));

                    // create new Location based on retrieve fields
                    Location location = new Location(timeStamp, macAdd, locationId);

                    // semanticPlace is same as origin, add it into resultList
                    if (semanticPlace.equals(origin)) {
                        resultList.add(location);
                    }
                }

                // if PreparedStatement is not null, close it
                if (pstmt != null) {
                    pstmt.close();
                }

                // if ResultSet is not null, close it
                if (rs != null) {
                    rs.close();
                }
            }

            // If resultset is not null, close it
            if (rs != null) {
                rs.close();
            }

            // If prepared statement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            // Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues
            System.out.println("Failed to prepare statement:" + e.getMessage());
        } finally {
            //Close connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        return resultList;
    }

    /**
     * <br />Attempts to batch insert a series of location records (usually maximum of
     * 50000, defined in LocationCSVController) It is much faster than adding
     * the records one by one. For comparative purpose, the speed when adding 1
     * by 1 is about 200 per second on a SSD, and this method speeds it up to
     * about 1600-2000 per second. Also, should not throw any error for primary
     * key duplication since we manually checked for duplicates early on.
     *
     * The LinkedHashMap contains values such as: [name] : [value] (e.g. as
     * follows) time_stamp : "2014-03-35 12:35:00" mac_address :
     * "abcdefghijabcdefghijabcdefghij12345678" location_id : "1023023323"
     *
     * @param conn Connection to be used
     * @param rowsToInsert ArrayList of LinkedHashMap which contains the list of
     * items to add into the database
     * @return true if successful or false if failed
     */
    public boolean batchCreate(Connection conn, ArrayList<LinkedHashMap<String, String>> rowsToInsert) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "INSERT into location VALUES (?,?,?,?)";

        try {
            //Reuse the connection from LocationCSV (we don't want to get from connection manager at this stage)
            //Reason being that when it gets connection from the connection manager, it tends to slow down.
            //We sped it up by coupling the DatabaseConnectionManager and Connection objects to LocationCSVController,
            //and assigning one dedicated Connection to LocationCSVController to use specifically for this method.

            conn.setAutoCommit(false);

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //Iterate through LinkedHashmap to 
            for (LinkedHashMap<String, String> rowToAdd : rowsToInsert) {

                //Pass parameters into prepared statement
                pstmt.setInt(1, Integer.parseInt(rowToAdd.get("rowId")));
                pstmt.setString(2, rowToAdd.get("time_stamp"));
                pstmt.setString(3, rowToAdd.get("mac_address"));
                pstmt.setInt(4, Integer.parseInt(rowToAdd.get("location_id")));

                //Add batch statement
                pstmt.addBatch();
            }

            //Execute batch statement
            pstmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            //Set status to false, since there is an error
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with batch Create:" + e);
        } finally {
            try {
                conn.setAutoCommit(true);
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error occurred with batch Create (set auto commit back to true):" + ex);
            }
        }
        return status;
    }

    /**
     * <br />Set all location records to zero once it is confirmed there is no
     * duplicate
     *
     * @param conn the Connection object to be used
     */
    public void setAllLocationRecordsToZero(Connection conn) {

        // Prepare SQL statement
        String sqlStatement = "UPDATE location SET rowId=0";

        try {
            // prepare statement
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);

            // Execute update (update)
            pstmt.executeUpdate();
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException ex) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues
            System.out.println(ex.getMessage());
            Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } finally {
            // Close preparedstatement if not null
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
                }
            }

            // Close ResultSet if not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * <br />Attempt to retrieve specific timeStamp, macAddress, locationId to check
     * whether there is duplicate in the database, if foundRecordNumber is
     * greater than 0, it means that it has just been entered recently and we
     * would want to retrieve its row number
     *
     * @param conn the shared Connection to be used
     * @param timeStamp the specified timestamp of the row
     * @param macAddress the specified mac-address of the row
     * @param locationId the specified locationId of the row
     * @param currentRowNumber the current line number that the row is at
     *
     * @return the row number of the previous line if there is duplicate, -1 if
     * no duplicate
     */
    public int checkDuplicateLocationIdAndReplace(Connection conn, String timeStamp, String macAddress, int locationId, int currentRowNumber) {
        int foundRecordNumber = -1;

        // Prepare the SQL statement
        String sqlStatement = "SELECT * FROM location WHERE time_stamp = ? AND mac_address= ?";
        try {
            // Prepare statement
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);

            // Set parameters into preparedstatement
            pstmt.setString(1, timeStamp);
            pstmt.setString(2, macAddress);
            //pstmt.setInt(4, 0);
            //System.out.println("Hello:"+pstmt.toString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // retrieve the field from ResultSet
                foundRecordNumber = rs.getInt(1);
            }

            // If preparedstatement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }

            // if foundrecordNumber exceeds 0, it means the previous row is inserted just recently and we want to set its rowId to the current row number.
            if (foundRecordNumber > 0) {
                sqlStatement = "UPDATE location SET rowId = ? WHERE rowId = ?";
                pstmt = conn.prepareStatement(sqlStatement);
                pstmt.setInt(1, currentRowNumber);
                pstmt.setInt(2, foundRecordNumber);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } finally {
            // Close the preparedStatement if not null
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex.getMessage());
                }
            }
            // Close the ResultSet if not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LocationDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return foundRecordNumber;
    }

    /**
     * <br />Retrieve the k most popular place (highest number of users) based on a
     * specific startDateTime and endDateTime
     *
     * @param startDateTime the query start date time
     * @param endDateTime the query end date time
     *
     * @return a TreeMap<Integer, ArrayList<String>> which has the number of
     * users as key and a ArrayList of semantic-place as values, empty TreeMap
     * if absent
     */
    public TreeMap<Integer, ArrayList<String>> getTopKPopularPlace(String startDateTime, String endDateTime) {
        //Instantiate LinkedHashMap and TreeMap to be returned at the end of the function
        LinkedHashMap<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
        TreeMap<Integer, ArrayList<String>> toReturnMap = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

        // prepare SQL statement
        String stmt = "SELECT DISTINCT(mac_address) FROM location `l`, locationlookup `ll` WHERE l.location_id = ll.location_id AND time_stamp > ? AND time_stamp <= ?";

        try {
            //Retrieves connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare statement
            pstmt = conn.prepareStatement(stmt);

            //First DateTime specified by user
            pstmt.setString(1, startDateTime);

            //Second datetime specified 
            pstmt.setString(2, endDateTime);

            //Store resultset
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // retrieev field from ResultSet
                String macAddress = rs.getString(1);

                // Prepare SQL statment
                stmt = "SELECT ll.semantic_place FROM location `l`, locationlookup `ll` WHERE l.location_id = ll.location_id AND time_stamp > ? AND time_stamp <= ? AND l.mac_address = ? ORDER BY l.time_stamp desc LIMIT 1";

                try {
                    //Prepare statement
                    PreparedStatement secondPstmt = conn.prepareStatement(stmt);

                    //First DateTime specified by user
                    secondPstmt.setString(1, startDateTime);

                    //Second datetime specified 
                    secondPstmt.setString(2, endDateTime);

                    //macAddress from previous query
                    secondPstmt.setString(3, macAddress);

                    //Instantiate and execute query.
                    ResultSet rs2 = secondPstmt.executeQuery();

                    if (rs2 != null) {
                        while (rs2.next()) {
                            // retrieve field from ResultSet
                            String semanticPlace = rs2.getString(1);
                            if (resultMap.get(semanticPlace) == null) {
                                resultMap.put(semanticPlace, 1);
                            } else {
                                int semanticPlaceCount = resultMap.get(semanticPlace);
                                resultMap.put(semanticPlace, ++semanticPlaceCount);
                            }
                        }
                    }

                    // Close the ResultSet if it is not null
                    if (rs2 != null) {
                        rs2.close();
                    }

                    // Close the second PreparedStatement if it is not null
                    if (secondPstmt != null) {
                        secondPstmt.close();
                    }

                } catch (SQLException e) {
                    System.out.println("Failed to prepare statement:" + e);
                }
            }

            // Attempt to convert from resultMap to toReturnMap by changing its key and values
            Set<String> semanticPlaceSet = resultMap.keySet();
            for (String semanticPlace : semanticPlaceSet) {
                int semanticPlaceCount = resultMap.get(semanticPlace);
                if (toReturnMap.get(semanticPlaceCount) == null) {
                    ArrayList<String> semanticPlaceList = new ArrayList<String>();
                    semanticPlaceList.add(semanticPlace);
                    toReturnMap.put(semanticPlaceCount, semanticPlaceList);
                } else {
                    ArrayList<String> semanticPlaceList = toReturnMap.get(semanticPlaceCount);
                    semanticPlaceList.add(semanticPlace);
                    toReturnMap.put(semanticPlaceCount, semanticPlaceList);
                }
            }

            // if ResultSet is not null, close it
            if (rs != null) {
                rs.close();
            }
            // if PreparedStatement is not null, close it
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            // Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Finally return all the location lookups 
        return toReturnMap;
    }
}
