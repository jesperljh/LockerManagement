/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.LockerController;
import dao.LockerDAO;
import entity.Locker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jerome
 */
@WebServlet(urlPatterns = {"/clearLockerServlet"})
public class clearLockerServlet extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            //String cluster = request.getParameter("lockerCluster");
            
            String nb = request.getParameter("nb");

            ArrayList<String> sids = new ArrayList<String>();

            String id = request.getParameter("1");
            int count = 1;
            while (id != null) {
                //out.println("<p>" + id + "</p>");
                sids.add(id);
                //out.println("<p>" + id + "</p>");
                count++;
                id = request.getParameter(count + "");
            }            
            
            // check for users within a neighbourhood who have existing lockers and removes their lockers
            // returns list of sids whose lockers are not found
            LockerDAO lockerDAO = new LockerDAO();
            ArrayList<Locker> lockerList =  lockerDAO.retrieveLockersByNeighbourhood(nb);
            ArrayList<Locker> tempLockerList = new ArrayList<Locker>();
            for(Locker l : lockerList){
                for(String s : sids){
                    if(l.getTaken_by() != null && l.getTaken_by().equals(s)){
                        l.setTaken_by(null);
                        tempLockerList.add(l);
                    }
                }
            }
            lockerDAO.updateLockers(tempLockerList);
            //sids = removeLockerUsers(sids, nb);
            response.sendRedirect("managerRemoveUser.jsp");
            /*if(sids.isEmpty()){
                out.println("<p>Lockers Updated</p>");
            }
            out.println("</body>");
            out.println("</html>");*/
        }
    }

    protected ArrayList<String> removeLockerUsers(ArrayList<String> sids, String nb) {
        // Checks for User SIDs in <SID, Locker> hashMap
        LockerController lc = new LockerController();
        LockerDAO lockerDAO = new LockerDAO();
        ArrayList<String> temp_sids = new ArrayList<String>();

        java.util.HashMap<String, Locker> userLocker = lc.getLockerByUserMap(nb);
        if (!userLocker.isEmpty()) {
            for (String temp_sid : sids) {
                if (userLocker.containsKey(temp_sid)) {
                    // remove user locker
                    Locker temp_locker = userLocker.get(temp_sid);
                    temp_locker.setTaken_by(null);                    

                    lockerDAO.updateLocker(temp_locker);                    
                }else{
                    temp_sids.add(temp_sid);
                }
                
            }
            
        }
        return temp_sids;
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
