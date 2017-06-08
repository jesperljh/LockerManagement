/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.LockerController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jesper/Jerome
 */
@WebServlet(urlPatterns = {"/lockerClusterServlet"})
public class lockerClusterServlet extends HttpServlet {

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

            LockerController lc = new LockerController();
            //ArrayList<String> clusterNames = lc.getClusterNames();
            ArrayList<String> clusterNames = new ArrayList<String>();
            clusterNames.add("rat");
            clusterNames.add("ox");
            clusterNames.add("tiger");
            clusterNames.add("rabbit");
            clusterNames.add("dragon");
            clusterNames.add("snake");
            clusterNames.add("horse");
            clusterNames.add("sheep");
            clusterNames.add("monkey");
            clusterNames.add("rooster");
            clusterNames.add("dog");
            clusterNames.add("pig");

            HashMap<String, Integer> lockerCluster = new HashMap<String, Integer>();

            for (int i = 0; i < clusterNames.size(); i++) {
                String clusterName = clusterNames.get(i);
                String check = request.getParameter("check_" + clusterName);
                int count = 0;
                if (check != null) {
                    count = Integer.parseInt(request.getParameter(clusterName));
                }
                lockerCluster.put(clusterName, count); // cluster and range selected
            }


            String nb = request.getParameter("neighbourhood");
            boolean validateRequest = lc.checkFreeLockersInCluster(lockerCluster);
            
            boolean lcResult = false;
            if (validateRequest) {
                lcResult = lc.assignLockerToManager(nb, lockerCluster);
            }
            response.sendRedirect("bootstrap.jsp");
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
