package webservice;

import controller.TopKNextPlacesController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utility.JsonPrint;

/**
 * This is the web service for Json Top K Next Places
 * @author Eugene
 */

public class JsonTopKNextPlaces extends HttpServlet {

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
        if (request.getMethod() != null && (request.getMethod().equals("GET"))) {

            String token = request.getParameter("token");
            String origin = request.getParameter("origin");
            String dateTime = request.getParameter("date");
            String kValue = request.getParameter("k");
            
            TopKNextPlacesController tknpc = new TopKNextPlacesController();
            LinkedHashMap<String, Object> topKNextPlacesResults = tknpc.getTopKNextPlaces(origin, dateTime, kValue, token, true);

            PrintWriter out = response.getWriter();
            
            // Instantiate JsonPrint utility
            JsonPrint jsonPrint = new JsonPrint();
            
            // Get JSON result String
            String prettyJsonString = jsonPrint.prettyPrint(topKNextPlacesResults);
            
            // Print the JSON output
            out.print(prettyJsonString);
            
            // Close the writer
            out.close();
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

