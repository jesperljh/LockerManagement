/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.*;
import entity.Locker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jesper/Jerome
 */
public class LockerController {

    private LockerDAO lockerDAO;

    public LockerController() {
        lockerDAO = new LockerDAO();
    }

    public ArrayList<String> getClusterNames() {

        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        ArrayList<String> clusterNames = new ArrayList<String>();
        for (int i = 0; i < lockerList.size(); i++) {
            String name = lockerList.get(i).getCluster();
            if (!clusterNames.contains(name)) {
                clusterNames.add(name);
            }
        }
        return clusterNames;
    }

    public boolean checkFreeLockersInCluster(HashMap<String, Integer> lockerCluster) {

        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();

        for (Map.Entry m : lockerCluster.entrySet()) {

            String cluster = (m.getKey()).toString();
            int clusterSize = (Integer) m.getValue();

            for (int i = 0; i < lockerList.size(); i++) {
                if (lockerList.get(i).getCluster().equals(cluster)) {
                    if (lockerList.get(i).getNeighbourhood() == null) {
                        clusterSize--;
                    }
                }
            }
            // exit hashmap loop as there are not enough free lockers
            if (clusterSize > 0) {
                return false; // not enough free locker
            }
        }

        return true; // enough free locker
    }

    public boolean assignLockerToManager(String nb, HashMap<String, Integer> lockerCluster) {

        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();

        // loop through hashmap (key, value)
        //base on key you will know to which locker
        // update locker object
        for (Map.Entry m : lockerCluster.entrySet()) {

            String cluster = (m.getKey()).toString();
            int clusterSize = (Integer) m.getValue();
            while (clusterSize > 0) {
                for (int i = 0; i < lockerList.size(); i++) {
                    Locker l = lockerList.get(i);
                    if (l.getCluster().equals(cluster)) {
                        if (l.getNeighbourhood() == null) {
                            l.setNeighbourhood(nb);
                            clusterSize--;
                            break;
                        }
                    }
                }
            }
        }
        lockerDAO.updateLockers(lockerList);
        return true;
    }

}
