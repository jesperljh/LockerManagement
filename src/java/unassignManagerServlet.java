/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.DemographicsCSVController;
import controller.LockerController;
import dao.DemographicsDAO;
import dao.LockerDAO;
import dao.RequestDAO;
import entity.Locker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jesper
 */
@WebServlet(urlPatterns = {"/unassignManagerServlet"})
public class unassignManagerServlet extends HttpServlet {

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
            String sid = request.getParameter("sid");
            String neighbourhood = request.getParameter("neighbourhood");
            String choice = request.getParameter("choice");
            LockerController lockerCtrl = new LockerController();
            DemographicsCSVController demoCtrl = new DemographicsCSVController();
            demoCtrl.unassignManager(sid, neighbourhood);
            if (choice.equals("yes")) {
                HashMap<String, ArrayList<Locker>> clusterMap = lockerCtrl.getLockerClusterListByNeighbourhood(neighbourhood);
                for (Map.Entry<String, ArrayList<Locker>> entry : clusterMap.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<Locker> value = entry.getValue();
                    for (Locker l : value) {
                        if (l.getTaken_by() != null && !l.getTaken_by().equals("")) {
                            RequestDAO requestDAO = new RequestDAO();
                            requestDAO.updateRequests(l.getTaken_by(), l.getTaken_by());
                        }
                    }
                }
                lockerCtrl.unassignAllMembersFromNeighbourhood(neighbourhood);
                DemographicsDAO demoDAO = new DemographicsDAO();
                demoDAO.updateNeighbourhoodToNull(neighbourhood);
            }

            response.sendRedirect("admin.jsp");
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
