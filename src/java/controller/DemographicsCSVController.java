package controller;


import dao.*;
import entity.Demographics;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import utility.ErrorMessage;
import utility.Validation;

/**
 * DemographicsCSVController contains methods in validating the demographics csv file and inserting them into database if valid
 * @author Jesper
 */
public class DemographicsCSVController {

    /**
     * DemographicsDAO to be used to create, retrieve data from database
     */
    private DemographicsDAO demographicsDAO;
    
     /**
     * Constructor of DemographicsCSVController
     * Instantiate DemographicsDAO to be used
     */
    public DemographicsCSVController() {
        demographicsDAO = new DemographicsDAO();
    }
    
    public ArrayList<Demographics> getManagers(){
        ArrayList<Demographics> demoList = new ArrayList<Demographics>();
        demoList = demographicsDAO.retrieveByRole("manager");
        
        return demoList;
    }
    
    public Demographics getUser(String sid){
        Demographics demographic = null;
        demographic = demographicsDAO.retrieveBySid(sid);
        
        return demographic;
    }
    
    public boolean unassignManager(String sid, String nb) {
        return demographicsDAO.updateRole(sid, nb, "user");
    }
    
    
    public ArrayList<Demographics> getUsersByNeighbourHood(String nb){
        ArrayList<Demographics> demoFull = demographicsDAO.retrieveAll();
        ArrayList<Demographics> demoNb = new ArrayList<Demographics>();
        for(Demographics d : demoFull){
            if(d.getNeighbourhood().equals(nb)){
                demoNb.add(d);
            }            
        }        
        return demoNb;
    }
}
