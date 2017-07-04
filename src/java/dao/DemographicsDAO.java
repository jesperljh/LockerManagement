package dao;

import entity.Demographics;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import utility.DatabaseConnectionManager;

/**
 * DemographicsDAO is the data access object which contains methods needed to
 * access data from demographics table in database
 *
 * @author Jesper
 */
public class DemographicsDAO {

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
     * <br />Retrieve an ArrayList of unique Demographics based on specified
     * startDateTime and endDateTime
     *
     * @param endDateTime the queried end date time
     * @param startDateTime the queried start date time
     *
     * @return an ArrayList of unique Demographics from specified startDateTime
     * and endDateTime, empty ArrayList if no results found
     */
    public ArrayList<Demographics> getBasicLocReport(String endDateTime, String startDateTime) {
        //Declare ArrayList to be returned at the end of the function
        ArrayList<Demographics> demographicsList = new ArrayList<Demographics>();

        // Prepare SQL statement
        String stmt = "SELECT * FROM demographics `d`, location `l` WHERE d.mac_address = l.mac_address AND l.time_stamp > ? AND l.time_stamp <= ? GROUP BY d.mac_address";

        try {
            //Retrieves connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare the second SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Second datetime specified by user
            pstmt.setString(1, startDateTime);

            //First datetime specified by user
            pstmt.setString(2, endDateTime);

            System.out.println("First Statement:" + pstmt.toString());

            //Declare secondResultset
            rs = pstmt.executeQuery();

            //Run through the second data set which contains the location (linked to locationlookup)
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String sid = rs.getString(4);
                String gender = rs.getString(5);
                String role = rs.getString(8);
                String neighbourhood = rs.getString(9);

                //Initialize new Location based on retrieve fields
                Demographics demographics = new Demographics(macAddress, name, password, sid, gender, role, neighbourhood);

                //Add location to locationList
                demographicsList.add(demographics);
            }
            // Close ResultSet if not null
            if (rs != null) {
                rs.close();
            }
            // Close PreparedStatement if not null
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to get basic location report:" + e);
        } finally {
            //Close connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Finally return all the location lookups
        return demographicsList;
    }

    /**
     * <br />Retrieves all the Demographics from the database in ascending order
     *
     * @return ArrayList of Demographics, empty ArrayList if no records found
     */
    public ArrayList<Demographics> retrieveAll() {
        //Declare ArrayList to be returned at the end of the function
        ArrayList<Demographics> demographicsList = new ArrayList<Demographics>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics ORDER by name ASC";

        try {
            //Retrieves connection
            conn = DatabaseConnectionManager.getConnection();

            //Prepare the second SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Declare secondResultset
            ResultSet resultSet = pstmt.executeQuery();

            //Iterate through the records retrieved
            while (resultSet.next()) {

                //Store the results into variable
                String macAddress = resultSet.getString(1);
                String name = resultSet.getString(2);
                String password = resultSet.getString(3);
                String sid = resultSet.getString(4);
                String gender = resultSet.getString(5);
                String role = resultSet.getString(8);
                String neighbourhood = resultSet.getString(9);

                //Initialize new Demographics
                Demographics demographics = new Demographics(macAddress, name, password, sid, gender, role, neighbourhood);

                //Add demographics to demographics List
                demographicsList.add(demographics);

            }
            //Close resultset if not null
            if (rs != null) {
                rs.close();
            }
            //Close preparedStatement if not null
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after use
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Finally return the list of demographics
        return demographicsList;
    }

    /**
     * <br />Retrieves a Demographics based on email and password
     *
     * @param id the id of the user (we will retrieve from the database as
     * follows, id@*)
     * @param password the password of the user
     * @return One Demographics matching the email and password
     */
    public Demographics retrieve(String id, String password) {

        //Declare a demographics object as null
        Demographics demographics = null;

        //Prepare the SQL statement
        String stmt = "SELECT * FROM demographics WHERE sid = ? AND password = ?";
        try {

            //Get the connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare the statement
            pstmt = conn.prepareStatement(stmt);

            //Set the necessary parameters into the prepared statement
            pstmt.setString(1, id);
            pstmt.setString(2, password);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is a match
            if (rs.next()) {
                //Store records into variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Initialize a new object with the variables
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
            }

            //Close resultset if not null
            if (rs != null) {
                rs.close();
            }
            //Close preparedstatement if not null
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConenctionManager after use            
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demographics;
    }

    /**
     * <br />Retrieves a Demographics based on mac-address
     *
     * @param macAddress the macAddress of the specified user
     * 
     * @return the Demographics which contains the mac address
     */
    public Demographics retrieveByMacAddress(String macAddress) {
        //Declare a Demographics object as null
        Demographics demographics = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics WHERE mac_address = ?";
        try {
            //Get connection from databaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, macAddress);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            if (rs.next()) {

                //Then let's store the row's result into the variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Instantiate new Demographics object with the result
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
            }
            //If resultSet is not null, then close
            if (rs != null) {
                rs.close();
            }

            //If preparedStatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demographics;

    }

    /**
     * <br />Retrieves a Demographics based on mac-address (this method is optimized
     * to use 1 connection)
     *
     * @param conn the shared Connection to be used
     * @param macAddress the macAddress of the user
     * 
     * @return One Demographics matching the mac address
     */
    public Demographics retrieveByMacAddress(Connection conn, String macAddress) {
        //Declare a Demographics object as null
        Demographics demographics = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics WHERE mac_address = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, macAddress);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            if (rs.next()) {

                //Then let's store the row's result into the variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Instantiate new Demographics object with the result
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
            }
            //If resultSet is not null, then close
            if (rs != null) {
                rs.close();
            }

            //If preparedStatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demographics;

    }
    
    /**
     * <br />Retrieves a Demographics based on role (this method is optimized
     * to use 1 connection)
     *
     * @param conn the shared Connection to be used
     * @param macAddress the macAddress of the user
     * 
     * @return One Demographics matching the role
     */
    public ArrayList<Demographics> retrieveByRole (String role) {
        ArrayList<Demographics> demoList = new ArrayList<Demographics>();
        //Declare a Demographics object as null
        Demographics demographics = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics WHERE role = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, role);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) {

                //Then let's store the row's result into the variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Instantiate new Demographics object with the result
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
                demoList.add(demographics);
            }
            //If resultSet is not null, then close
            if (rs != null) {
                rs.close();
            }

            //If preparedStatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demoList;

    }

    /**
     * <br />Retrieves a Demographics based on sid (this method is optimized
     * to use 1 connection)
     *
     * @param conn the shared Connection to be used
     * @param macAddress the macAddress of the user
     * 
     * @return One Demographics matching the sid
     */
    public Demographics retrieveBySid (String sid) {
        //Declare a Demographics object as null
        Demographics demographics = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics WHERE sid = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, sid);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            if (rs.next()) {

                //Then let's store the row's result into the variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Instantiate new Demographics object with the result
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
            }
            //If resultSet is not null, then close
            if (rs != null) {
                rs.close();
            }

            //If preparedStatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demographics;

    }
    
    /**
     * <br />Updates a user in the Demographics table of role based on sid
     *
     * @param sid the sid of the user (key)
     * @param role the role of the user
     * 
     * @return true if successfully updated, false if failed to update (perhaps
     * mac address not found)
     */
    public boolean updateRole(String sid, String neighbourhood, String role) {
        //Assume status is  true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "UPDATE demographics "
                + "SET role=?,"
                + "neighbourhood=?"
                + "WHERE sid = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, role);
            pstmt.setString(2, neighbourhood);
            pstmt.setString(3, sid);

            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used            
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successfully updated or false if update fail
        return status;
    }
    
    /**
     * <br />Creates a user in the Demographics table based on specified macAddress, name, password, email and gender
     *
     * @param macAddress the macAddress of the user
     * @param name the macAddress of the user
     * @param password the macAddress of the user
     * @param email the macAddress of the user
     * @param gender the macAddress of the user
     * @param conn the connection to be reused
     * 
     * @return true if successfully added, false if failed to add (either
     * constraint error, or duplicate key)
     */
    public boolean create(Connection conn, String macAddress, String name, String password, String email, String gender) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "INSERT into demographics VALUES (?,?,?,?,?)";

        try {
            //Prpeare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, macAddress);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setString(5, gender);

            //Update prepared statement
            pstmt.executeUpdate();

            //If preparedstatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();            
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Error occurred with create:" + e);
        }

        //Returns true if successfully added or false if add fail
        return status;
    }

    /**
     * <br />Updates a user in the Demographics table based on macAddress, name, password, email (key) and gender
     *
     * @param macAddress the macAddress of the user
     * @param name the name of the user
     * @param password the password of the user
     * @param email the email of the user (key)
     * @param gender the gender of the user
     * 
     * @return true if successfully updated, false if failed to update (perhaps
     * mac address not found)
     */
    public boolean update(String macAddress, String name, String password, String email, String gender) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "UPDATE demographics "
                + "SET mac_address=?,"
                + "student_name=?,"
                + "student_password=?,"
                + "gender=? "
                + "WHERE email = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, macAddress);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, gender);
            pstmt.setString(5, email);

            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used            
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successfully updated or false if update fail
        return status;
    }
    
    public boolean updateNeighbourhoodToNull(String nb) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "UPDATE demographics "
                + "SET neighbourhood=? "
                + "WHERE neighbourhood = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, null);
            pstmt.setString(2, nb);
            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used            
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successfully updated or false if update fail
        return status;
    }

    /**
     * <br />Deletes a user in the Demographics table based on his email
     *
     * @param email the email of the user
     * @return true if successfully delete, false if failed to delete (perhaps
     * email not found, or database error)
     */
    public boolean delete(String email) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "DELETE from demographics WHERE email = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare PreparedStatement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, email);

            //Execute query
            pstmt.executeUpdate();

            //If preparedstatement is not null, let's close it
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();            
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                                            
            System.out.println("Error occurred with delete:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successfully deleted or false if delete fail
        return status;
    }
    
    
    
    
    /* added by jerome 
        *
        * needs commenting
        *
        *
        */

    public ArrayList<Demographics> retrieveByNeighbourhood(String nhood) {
        
        //Declare a Demographics object as null            
        ArrayList<Demographics> demoList = new ArrayList<Demographics>();
        //Declare a Demographics object as null        
        Demographics demographics = null;

        //Prepare SQL statement
        String stmt = "SELECT * FROM demographics WHERE neighbourhood = ? ORDER by name ASC";
        try {
            //Get connection from databaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, nhood);

            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) {

                //Then let's store the row's result into the variables
                String macAddressR = rs.getString(1);
                String nameR = rs.getString(2);
                String passwordR = rs.getString(3);
                String sidR = rs.getString(4);
                String genderR = rs.getString(5);
                String roleR = rs.getString(8);
                String neighbourhoodR = rs.getString(9);

                //Instantiate new Demographics object with the result
                demographics = new Demographics(macAddressR, nameR, passwordR, sidR, genderR, roleR, neighbourhoodR);
                //Add demographics to demographics List
                demoList.add(demographics);
            }
            //If resultSet is not null, then close
            if (rs != null) {
                rs.close();
            }

            //If preparedStatement is not null, then close
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                        
            System.out.println("Failed to prepare statement:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Return demographics object if there is a found user (or null if no user found)
        return demoList;

    }
    
    
    
    
    public boolean updateDemoBySID(String sid, String neighbourhood, String managerSID) {
        //Assume status is  true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "UPDATE demographics "
                + "SET neighbourhood_manager=?,"
                + "neighbourhood=?"
                + "WHERE sid = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, managerSID);
            pstmt.setString(2, neighbourhood);
            pstmt.setString(3, sid);

            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used            
            DatabaseConnectionManager.closeConnection(conn);
        }
        //Returns true if successfully updated or false if update fail
        return status;
    }

}
