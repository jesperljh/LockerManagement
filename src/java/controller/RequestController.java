/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.LockerDAO;
import dao.RequestDAO;
import entity.Locker;
import entity.Request;
import java.util.ArrayList;

/**
 *
 * @author Jesper
 */
public class RequestController {

    private RequestDAO requestDAO;

    public RequestController() {
        requestDAO = new RequestDAO();
    }

    public ArrayList<Request> getRequestsBySid(String sid) {

        ArrayList<Request> requestList = requestDAO.retrieveRequests();
        ArrayList<Request> tempList = new ArrayList<Request>();
        for (Request r : requestList) {
            if (r.getReceiver() != null && r.getReceiver().equals(sid)) {
                if (r.getStatus() != null && r.getStatus().equals("pending")) {
                    tempList.add(r);
                }
            }
        }
        return tempList;
    }

    public boolean acceptRequest(int id, String mySid, String rSid) {
        RequestDAO requestDAO = new RequestDAO();
        boolean update = false;
        update = requestDAO.updateRequest(id, "accept", mySid);
        LockerDAO lockerDAO = new LockerDAO();
        
        Locker locker1 = lockerDAO.retrieveLockersBySid(mySid);
        Locker locker2 = lockerDAO.retrieveLockersBySid(rSid);
        String temp = locker1.getTaken_by();
        locker1.setTaken_by(locker2.getTaken_by());
        locker2.setTaken_by(temp);
        lockerDAO.updateLocker(locker1);
        lockerDAO.updateLocker(locker2);
        
        return update;
    }

    public boolean rejectRequest(int id, String mySid) {
        RequestDAO requestDAO = new RequestDAO();
        boolean update = false;
        update = requestDAO.updateRequest(id, "reject", mySid);

        return update;
    }
}
