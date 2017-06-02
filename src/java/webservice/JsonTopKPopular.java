package webservice;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import controller.TopKPopularPlaceController;
import java.util.LinkedHashMap;
import utility.JsonPrint;

/**
 * This is the web service for Json Top K Popular Places
 * @author Eugene
 */
public class JsonTopKPopular extends HttpServlet {

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

            // Instantiate token specified by token
            String token = request.getParameter("token");
            
            // Instantiate k value specified by user
            String k = request.getParameter("k");

            // Instantiate date specified by user
            String date = request.getParameter("date");

            // Instatiate writer
            PrintWriter out = response.getWriter();
            
            // Instantiate TopKPopularPlaceController
            TopKPopularPlaceController tkpc = new TopKPopularPlaceController();

            // Retrieve and instantiate the results from TopKPopularPlaceController
            LinkedHashMap<String, Object> popularPlaceResult = tkpc.getTopKPopularPlace(k, date, token, true);
            

            // Instantiate JsonPrint utility
            JsonPrint jsonPrint = new JsonPrint();
            
            // Get JSON result String
            String prettyJsonString = jsonPrint.prettyPrint(popularPlaceResult);
            
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
