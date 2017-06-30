/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.LockerController;
import controller.RequestController;
import dao.DemographicsDAO;
import dao.LockerDAO;
import dao.RequestDAO;
import entity.Demographics;
import entity.Locker;
import entity.Request;
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
@WebServlet(urlPatterns = {"/registerUserServlet"})
public class registerUserServlet extends HttpServlet {

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
            
            String sid = request.getParameter("user_sid");
            String mgrSID = request.getParameter("mgr_sid");
            String neighbourhood = request.getParameter("nb");
            out.println("<script>alert('sid=" + sid + ", nb= " + neighbourhood + ", mgrSID=" + mgrSID + "');</script>");
            // Change Neighbourhood
            DemographicsDAO demoDAO = new DemographicsDAO();
            demoDAO.updateDemoBySID(sid, neighbourhood, mgrSID);
            
            
            // Remove Locker
            LockerController lCtrl = new LockerController();
            Locker l = lCtrl.getLockerBySid(sid);
            if(l != null){
                l.setTaken_by(null);            
            
                LockerDAO lDAO = new LockerDAO();
                lDAO.updateLocker(l);
            }
            
            // Reject Outstanding Requests            
            RequestDAO rqDAO = new RequestDAO ();
            rqDAO.updateRequests(sid, sid);

            
            response.sendRedirect("managerRegisterUser.jsp");
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
