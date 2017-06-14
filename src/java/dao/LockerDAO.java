/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Demographics;
import entity.Locker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import utility.DatabaseConnectionManager;

/**
 *
 * @author Jesper/Jerome
 */
public class LockerDAO {

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
     * @return One Demographics matching the sid
     */
    public ArrayList<Locker> retrieveLockers() {
        //Declare a Demographics object as null
        Locker locker = null;
        ArrayList<Locker> lockerList = new ArrayList<Locker>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM lockers";
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
                String cluster = rs.getString(2);
                String locker_no = rs.getString(3);
                String taken_by = rs.getString(4);
                String neighbourhood = rs.getString(5);

                //Instantiate new Demographics object with the result
                locker = new Locker(id, cluster, locker_no, taken_by, neighbourhood);
                lockerList.add(locker);
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
        return lockerList;

    }
    
    
    
    
    public ArrayList<Locker> retrieveLockersByNeighbourhood(String nb) {
        //Declare a Demographics object as null
        Locker locker = null;
        ArrayList<Locker> lockerList = new ArrayList<Locker>();

        //Prepare SQL statement
        String stmt = "SELECT * FROM lockers WHERE neighbourhood=?";
        try {
            //Get connection from DatabaseConnectionManager
            conn = DatabaseConnectionManager.getConnection();

            //Prepare SQL statement
            pstmt = conn.prepareStatement(stmt);
            pstmt.setString(1, nb); 

            //Set parameters into prepared statement
            //pstmt.setString(1, sid);
            //Execute query
            rs = pstmt.executeQuery();

            //If there is results
            while (rs.next()) { //while
                int id = rs.getInt(1);
                String cluster = rs.getString(2);
                String locker_no = rs.getString(3);
                String taken_by = rs.getString(4);
                String neighbourhood = rs.getString(5);

                //Instantiate new Demographics object with the result
                locker = new Locker(id, cluster, locker_no, taken_by, neighbourhood);
                lockerList.add(locker);
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
        return lockerList;

    }
    
    
    

    public boolean updateLockers(ArrayList<Locker> lockerList) {
        //Assume status is  true, set false only if exception is caught
        boolean status = true;
        for (Locker l : lockerList) {
            //Prepare SQL statement
            String stmt = "";
            if (l.getNeighbourhood() == null) {
                stmt = "UPDATE lockers "
                        + "SET cluster=?,"
                        + "neighbourhood = NULL"
                        + " WHERE id = ?";
            } else {
                //Prepare SQL statement
                stmt = "UPDATE lockers "
                        + "SET cluster=?,"
                        + "neighbourhood=?"
                        + " WHERE id = ?";
            }

            try {
                //Get connection from DatabaseConnectionManager
                conn = DatabaseConnectionManager.getConnection();

                //Prepare prepared statement
                pstmt = conn.prepareStatement(stmt);
                if (l.getNeighbourhood() == null) {
                    //Set parameters into prepared statement
                    pstmt.setString(1, l.getCluster());
                    pstmt.setInt(2, l.getId());
                } else {
                    //Set parameters into prepared statement
                    pstmt.setString(1, l.getCluster());
                    pstmt.setString(2, l.getNeighbourhood());
                    pstmt.setInt(3, l.getId());
                }

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
        }
        //Returns true if successfully updated or false if update fail
        return status;

    }

}
