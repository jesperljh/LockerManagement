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
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet assignLockerServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet assignLockerServlet at " + request.getContextPath() + "</h1>");

            String cluster = request.getParameter("lockerCluster");
            String nb = request.getParameter("nb");

            DemographicsCSVController demoCtrl = new DemographicsCSVController();
            //ArrayList<Demographics> demoNb = demoCtrl.getUsersByNeighbourHood(nb);
            ArrayList<String> sids = new ArrayList<String>();

            String id = request.getParameter("1");
            int count = 1;
            while (id != null) {
                out.println("<p>" + id + "</p>");
                sids.add(id);
                count++;
                id = request.getParameter(count + "");
            }

            LockerController lc = new LockerController();

            HashMap<String, ArrayList<Locker>> occupiedlockerCluster = lc.getLockersWithPeople(nb);
            HashMap<String, Locker> userLocker = lc.getLockerByUserMap(nb);
            for (String sid : sids) {
                if (userLocker.containsKey(sid)) {
                    sids.remove(sid);
                    out.println("<p>Duplicate SID found in list</p>");
                    out.println("<p>Removing SID: " + sid + "</p>");
                }
            }

            HashMap<String, ArrayList<Locker>> freelockerCluster = lc.getLockersWithoutPeople(nb);

            ArrayList<Locker> freelockerList = freelockerCluster.get(cluster);
            if (freelockerList != null) {
                out.println("<p>Size Available:" + freelockerList.size() + "</p>");
                out.println("<p>People Selected:" + sids.size() + "</p>");
                if (freelockerList.size() < sids.size()) {
                    request.setAttribute("error", "error message");
                    out.println("<p>Not Enough free lockers</p>");
                    out.println("</body>");
                    out.println("</html>");
                    //response.sendRedirect("manager.jsp");

                } else {
                    LockerDAO lockerDAO = new LockerDAO();
                    for (int i = 0; i < sids.size(); i++) {
                        freelockerList.get(i).setTaken_by(sids.get(i));
                    }
                    lockerDAO.updateLockers(freelockerList);
                    out.println("<p>Lockers Assigned</p>");
                    out.println("</body>");
                    out.println("</html>");
                    //response.sendRedirect("manager.jsp");
                }
            } else {
                out.println("<p>No free lockers left</p>");
            }

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
