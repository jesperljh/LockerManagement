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

    public HashMap<String, ArrayList<Locker>> getLockerClusterList() {

        // Creates a sublist of lockers based on a single cluster name
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        HashMap<String, ArrayList<Locker>> lockerClusterMap = new HashMap<String, ArrayList<Locker>>();

        for (Locker l : lockerList) {
            String clusterName = l.getCluster();
            ArrayList<Locker> subLockers = lockerClusterMap.get(clusterName);
            if (subLockers == null) {
                ArrayList<Locker> tempLockerList = new ArrayList<Locker>();
                tempLockerList.add(l);
                lockerClusterMap.put(clusterName, tempLockerList);
            } else {
                subLockers.add(l);
                lockerClusterMap.put(clusterName, subLockers);
            }
        }
        return lockerClusterMap;
    }

    public HashMap<String, ArrayList<Locker>> getLockerClusterListByNeighbourhood(String nb) {

        // Creates a sublist of lockers based on a single cluster name
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockersByNeighbourhood(nb);
        HashMap<String, ArrayList<Locker>> lockerClusterMap = new HashMap<String, ArrayList<Locker>>();
        //HashMap<String, Integer> lockerClusterMap2 = new HashMap<String, Integer>();
        for (Locker l : lockerList) {
            String clusterName = l.getCluster();
            ArrayList<Locker> subLockers = lockerClusterMap.get(clusterName);
            //int count = lockerClusterMap2.get(clusterName);
            if (subLockers == null) {
                ArrayList<Locker> tempLockerList = new ArrayList<Locker>();
                tempLockerList.add(l);
                lockerClusterMap.put(clusterName, tempLockerList);
                //lockerClusterMap2.put(clusterName, 1);
            } else {
                subLockers.add(l);
                lockerClusterMap.put(clusterName, subLockers);
                //lockerClusterMap2.put(clusterName, count + 1);
            }
        }
        return lockerClusterMap;
    }

    public HashMap<String, ArrayList<Locker>> getLockerWithoutNHood() {

        // Creates a sublist of lockers based on a single cluster name
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        HashMap<String, ArrayList<Locker>> lockerClusterMap = new HashMap<String, ArrayList<Locker>>();

        for (Locker l : lockerList) {
            String clusterName = l.getCluster();
            if (l.getNeighbourhood() == null) {
                ArrayList<Locker> subLockers = lockerClusterMap.get(clusterName);

                if (subLockers == null) {
                    ArrayList<Locker> tempLockerList = new ArrayList<Locker>();
                    tempLockerList.add(l);
                    lockerClusterMap.put(clusterName, tempLockerList);
                } else {
                    subLockers.add(l);
                    lockerClusterMap.put(clusterName, subLockers);
                }
            }
        }
        return lockerClusterMap;
    }

    public ArrayList<Locker> getLockersWithoutPeopleInNeighbourhood(String nb) {
        LockerDAO lockerDAO = new LockerDAO();
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        ArrayList<Locker> lockerList2 = new ArrayList<Locker>();
        for(Locker l : lockerList){
            if(l.getNeighbourhood() != null && l.getNeighbourhood().equals(nb)){
                if(l.getTaken_by() == null){
                    lockerList2.add(l);
                }
            }
        }
        return lockerList2;
    }

    public ArrayList<Locker> getLockersWithPeopleInNeighbourhood(String nb) {
        
        LockerDAO lockerDAO = new LockerDAO();
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        ArrayList<Locker> lockerList2 = new ArrayList<Locker>();
        for(Locker l : lockerList){
            if(l.getNeighbourhood() != null && l.getNeighbourhood().equals(nb)){
                if(l.getTaken_by() != null){
                    lockerList2.add(l);
                }
            }
        }
        return lockerList2;
    }

    public HashMap<String, Locker> getLockerByUserMap(String nb) {

        HashMap<String, Locker> userLockerMap = new HashMap<String, Locker>();
        /*HashMap<String, ArrayList<Locker>> occupiedlockerCluster = getLockersWithPeople(nb);

        for (Map.Entry<String, ArrayList<Locker>> entry : occupiedlockerCluster.entrySet()) {
            ArrayList<Locker> value = entry.getValue();
            if (value != null) {
                for (Locker l : value) {
                    userLockerMap.put(l.getTaken_by(), l);
                }
            }
        }*/
        return userLockerMap;
    }

    public Integer countFreeLockers(ArrayList<Locker> lockerList) {

        int count = 0;
        for (Locker l : lockerList) {
            if (l.getNeighbourhood() == null) {
                count++;
            }
        }

        return count;
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

    public boolean clearNeighbourhoodByCluster(String nb, String cstr) {

        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();

        // narrow down locker list by cluster
        //ArrayList<Locker> subList = getLockerClusterList(lockerList, cstr);

        /*for (Locker l : subList) {
            //check cluster nieghbourhood pair
            if (l.getCluster().equals(cstr)) {
                if (l.getNeighbourhood().equals(nb)) {
                    l.setNeighbourhood(null);
                    l.setTaken_by(null); // forcibly remove occupant as well
                }
            }
        }
        // send only narrowed down list
        lockerDAO.updateLockers(subList);*/
        return true;
    }

    public boolean unassignAllMembersFromNeighbourhood(String nb) {
        LockerDAO lockerDAO = new LockerDAO();
        ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
        for (Locker l : lockerList) {
            if (l.getNeighbourhood() != null && l.getNeighbourhood().equals(nb)) {
                l.setNeighbourhood(null);
            }
        }
        lockerDAO.updateLockers(lockerList);
        return true;
    }

}
