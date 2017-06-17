/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.DemographicsCSVController;
import controller.LockerController;
import dao.DemographicsDAO;
import dao.LockerDAO;
import entity.Demographics;
import entity.Locker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jerome
 */
@WebServlet(urlPatterns = {"/assignLockerServlet"})
public class assignLockerServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            String cluster = request.getParameter("lockerCluster");
            String nb = request.getParameter("nb");

            ArrayList<String> sids = new ArrayList<String>();

            String id = request.getParameter("1");
            int count = 1;
            while (id != null) {
                //out.println("<p>" + id + "</p>");
                sids.add(id);
                count++;
                id = request.getParameter(count + "");
            }
            // check for users who have existing lockers
            sids = removeDuplicateSID(sids, nb); 
            
            // assign users lockers, return user sids who were not assigned a locker
            sids = setLockerUsers(sids, nb, cluster);
        }
    }

    protected ArrayList<String> removeDuplicateSID(ArrayList<String> sids, String nb) {
        // Checks for User SIDs in <SID, Locker> hashMap
        LockerController lc = new LockerController();

        HashMap<String, Locker> userLocker = lc.getLockerByUserMap(nb);
        for (String sid : sids) {
            if (userLocker.containsKey(sid)) {
                sids.remove(sid);
            }
        }
        return sids;
    }

    protected ArrayList<String> setLockerUsers(ArrayList<String> sids, String nb, String cluster) {
        
        // Sets the Locker Taken_by field and passes it back to DAO
        LockerController lc = new LockerController();
        LockerDAO lockerDAO = new LockerDAO();
        
        HashMap<String, ArrayList<Locker>> freelockerCluster = lc.getLockersWithoutPeople(nb);
        ArrayList<Locker> freelockerList = freelockerCluster.get(cluster);
        ArrayList<Locker> templockerList = new ArrayList<Locker>();

        if (freelockerList != null) {
            
            if (freelockerList.size() < sids.size()) {
                // Fill up the free lockers and return the remaining SIDs
                
                while(freelockerList.size()>0){
                    
                    String temp_sid = sids.get(0);
                    Locker temp_locker = freelockerList.get(0);
                    
                    temp_locker.setTaken_by(temp_sid);
                    templockerList.add(temp_locker);
                    
                    sids.remove(0);
                    freelockerList.remove(0);                    
                }
                lockerDAO.updateLockers(templockerList);
                return sids;

            } else {
                // Fill up the free lockers until SIDs run out                
                for (int i = 0; i < sids.size(); i++) {
                    freelockerList.get(i).setTaken_by(sids.get(i));
                }
                lockerDAO.updateLockers(freelockerList);
                return null; // all SIDs used up, return empty list
            }
        } else {
            return sids;
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
