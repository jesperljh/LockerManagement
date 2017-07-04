/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 * @author Default
 */
@WebServlet(urlPatterns = {"/lockerRequestServlet"})
public class lockerRequestServlet extends HttpServlet {

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
            String selectedLocker = request.getParameter("selectedLocker");
            String requester = request.getParameter("requester");
            String cluster = request.getParameter("myCluster");
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
            
            for(String s : clusterNames){
                if(cluster.equals(s)){
                    if(cluster.equals("ox")){
                        int lockerNo = Integer.parseInt(selectedLocker) + 24;
                        selectedLocker = "A" + lockerNo;
                    }else if(cluster.equals("rat")){
                        selectedLocker = "A" + selectedLocker;
                    }else if(cluster.equals("tiger")){
                        selectedLocker = "B" + selectedLocker;
                    }else if(cluster.equals("rabbit")){
                        selectedLocker = "C" + selectedLocker;
                    }else if(cluster.equals("dragon")){
                        selectedLocker = "D" + selectedLocker;
                    }else if(cluster.equals("snake")){
                        selectedLocker = "E" + selectedLocker;
                    }else if(cluster.equals("hourse")){
                        selectedLocker = "F" + selectedLocker;
                    }else if(cluster.equals("sheep")){
                        selectedLocker = "G" + selectedLocker;
                    }else if(cluster.equals("monkey")){
                        selectedLocker = "H" + selectedLocker;
                    }else if(cluster.equals("rooster")){
                        selectedLocker = "I" + selectedLocker;
                    }else if(cluster.equals("dog")){
                        selectedLocker = "K" + selectedLocker;
                    }else if(cluster.equals("pig")){
                        selectedLocker = "L" + selectedLocker;
                    }
                }
            }
            
            LockerDAO lockerDAO = new LockerDAO();
            ArrayList<Locker> lockerList = lockerDAO.retrieveLockers();
            String receiver = "";
            for(Locker l : lockerList){
                if(l.getLocker_no() != null && l.getLocker_no().equals(selectedLocker)){
                    receiver = l.getTaken_by();
                }
            }
            if(receiver == null || receiver.equals("")){
                lockerDAO.updateLockerToNull(requester);
                lockerDAO.updateLockerToSid(requester, selectedLocker);
            }else if(selectedLocker != null && !selectedLocker.equals("")){
                RequestDAO requestDAO = new RequestDAO();
                requestDAO.insertRequest(requester, receiver, selectedLocker);
            }
            
            response.sendRedirect("user.jsp");
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
