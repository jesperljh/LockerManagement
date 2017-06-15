/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import dao.LockerDAO;
import entity.Locker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author Default
 */
@WebServlet(urlPatterns = {"/getLockerClusterListByNeighbourhood"})
public class getLockerClusterListByNeighbourhood extends HttpServlet {

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

            String nb = request.getParameter("neightbourhood").trim();
            if (nb == null || nb.equals("")) {
                nb = "Guest";
            }

            LockerDAO lockerDAO = new LockerDAO();
            // Creates a sublist of lockers based on a single cluster name
            ArrayList<Locker> lockerList = lockerDAO.retrieveLockersByNeighbourhood(nb);
            HashMap<String, ArrayList<Locker>> lockerClusterMap = new HashMap<String, ArrayList<Locker>>();
            HashMap<String, Integer> lockerClusterMap2 = new HashMap<String, Integer>();
            for (Locker l : lockerList) {
                String clusterName = l.getCluster();
                ArrayList<Locker> subLockers = lockerClusterMap.get(clusterName);
                Integer count = lockerClusterMap2.get(clusterName);
                if (subLockers == null) {
                    ArrayList<Locker> tempLockerList = new ArrayList<Locker>();
                    tempLockerList.add(l);
                    lockerClusterMap.put(clusterName, tempLockerList);
                    lockerClusterMap2.put(clusterName, 1);
                } else {
                    subLockers.add(l);
                    lockerClusterMap.put(clusterName, subLockers);
                    lockerClusterMap2.put(clusterName, count + 1);
                }
            }
            response.setContentType("application/json;charset=utf-8");
            int[] sampleData = null;
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();
            JSONObject member = new JSONObject();

            for (Map.Entry<String, Integer> entry : lockerClusterMap2.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                member.put(key, value);

                // do what you have to do here
                // In your case, an other loop.
            }
            //member.put("", sampleData);

            array.add(member);
            json.put("jsonArray", array);

            PrintWriter pw = response.getWriter();
            pw.print(json.toString());
            pw.close();
            //Gson gson = new Gson();
            //String json = gson.toJson(lockerList);

            //response.getWriter().write(json);
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
