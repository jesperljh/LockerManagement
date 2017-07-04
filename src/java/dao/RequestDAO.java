/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Locker;
import entity.Request;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import utility.DatabaseConnectionManager;

/**
 *
 * @author Default
 */
public class RequestDAO {

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
     * <br />Retrieves a Demographics based on sid (this method is optimized to
     * use 1 connection)
     *
     * @param conn the shared Connection to be used
     * @param macAddress the macAddress of the user
     *
     * @return a list of Request
     */
    public ArrayList<Request> retrieveRequests() {
        //Declare a Demographics object as null
        Request request = null;
        ArrayList<Request> requestList = new ArrayList<Request>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM requests";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            //pstmt.setString(1, sid);
            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) { //while
                int id = rs.getInt(1);
                String requester = rs.getString(2);
                String receiver = rs.getString(3);
                String status = rs.getString(4);
                String lockerNo = rs.getString(5);

                //Instantiate new Demographics object with the result
                request = new Request(id, requester, receiver, status, lockerNo);
                requestList.add(request);
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
        return requestList;

    }

    /**
     * <br />Retrieves a Demographics based on sid (this method is optimized to
     * use 1 connection)
     *
     * @param conn the shared Connection to be used
     * @param macAddress the macAddress of the user
     *
     * @return a list of Request
     */
    public ArrayList<Request> retrieveRequestsByReceiver(String mySid) {
        //Declare a Demographics object as null
        Request request = null;
        ArrayList<Request> requestList = new ArrayList<Request>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM requests where receiver = ? AND request_status = ?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);
            pstmt.setString(1, mySid);
            pstmt.setString(2, "pending");

            //Set parameters into prepared statement
            //pstmt.setString(1, sid);
            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) { //while
                int id = rs.getInt(1);
                String requester = rs.getString(2);
                String receiver = rs.getString(3);
                String status = rs.getString(4);
                String lockerNo = rs.getString(5);

                //Instantiate new Demographics object with the result
                request = new Request(id, requester, receiver, status, lockerNo);
                requestList.add(request);
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
        return requestList;

    }

    public boolean updateRequest(int id, String action, String mySid, String rSid) {
        //Assume status is  true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "";
        stmt = "UPDATE requests "
                + "SET request_status = ?"
                + " WHERE id = ?";

        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, action);
            pstmt.setInt(2, id);

            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }
            //end of for loop

        } catch (SQLException e) {
            //Set status to false, since there is an error in executing pstmt.executeUpdate();
            status = false;
            //Prints out SQLException - good for debugging if sql statement is buggy or constraints that may be causing issues                                    
            System.out.println("Error occurred with update:" + e);
        } finally {
            //Close the connection from DatabaseConnectionManager after used            
            DatabaseConnectionManager.closeConnection(conn);
        }

        if (action.equals("accept")) {
            updateRequests(mySid, rSid);
        }

        //Returns true if successfully updated or false if update fail
        return status;

    }

    public boolean updateRequests(String mySid, String rSid) {
        //Assume status is  true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "";
        stmt = "UPDATE requests "
                + "SET request_status = ?"
                + " WHERE receiver = ? OR receiver = ? OR requester = ? OR requester = ?";

        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare prepared statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, "reject");
            pstmt.setString(2, mySid);
            pstmt.setString(3, rSid);
            pstmt.setString(4, rSid);
            pstmt.setString(5, mySid);

            //Execute update
            pstmt.executeUpdate();

            //If prepared statement is not null, close
            if (pstmt != null) {
                pstmt.close();
            }
            //end of for loop

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
    
    public boolean insertRequest(String requester, String receiver, String lockerNo) {
        //Assume status is true, set false only if exception is caught
        boolean status = true;

        //Prepare SQL statement
        String stmt = "INSERT into requests (requester, receiver, request_status, lockerNo) VALUES (?,?,?,?);";

        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();
                    
            //Prpeare statement
            pstmt = conn.prepareStatement(stmt);

            //Set parameters into prepared statement
            pstmt.setString(1, requester);
            pstmt.setString(2, receiver);
            pstmt.setString(3, "pending");
            pstmt.setString(4, lockerNo);

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
}
