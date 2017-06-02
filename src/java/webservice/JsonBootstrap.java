package webservice;

import controller.ProcessFileController;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import utility.ErrorMessage;
import utility.JsonPrint;
import utility.TokenValidation;

/**
 * This is the web service for Json Bootstrap
 * @author Kenneth
 */
public class JsonBootstrap extends HttpServlet {

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
        String sourcePage = null;
        String token = null;
        LinkedHashMap<String, Object> bootstrapResult = null;

        if (request.getMethod() != null && (request.getMethod().equals("POST"))) {

            // all the code involved in this "if block" make use of the apache commons external library
            if (ServletFileUpload.isMultipartContent(request)) {

                // a repository is a directory inside the server/machine where you store your uploaded file
                File repository = null;
                try {
                    // Create a factory for disk-based file items
                    DiskFileItemFactory factory = new DiskFileItemFactory();

                    // Check whether the openshift temporary folder is available
                    if (System.getenv("OPENSHIFT_DATA_DIR") == null) {
                        // for local machine
                        repository = new File("C://uploads");
                        if (!repository.exists()) {
                            repository.mkdir();
                        }
                    } else {
                        // for openshift 
                        repository = new File(System.getenv("OPENSHIFT_DATA_DIR"));
                    }

                    // assign the repository (directory) to the factory (the drive)
                    factory.setRepository(repository);

                    // Create a new file upload handler
                    ServletFileUpload upload = new ServletFileUpload(factory);

                    // Parse the request to retrieve a list of fileitem
                    // each item may be a key/value pair from the form post or an uploaded file
                    List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));

                    //Let's check for token here
                    for (FileItem item : items) {
                        if (item.isFormField()) {
                            if (item.getFieldName().equals("page")) {
                                // item.getString() retrieves the value of the field (based on the fieldName)
                                sourcePage = item.getString();
                            }
                            // this if-block is to check the validity of the token
                            if (item.getFieldName().equals("token")) {

                                // basically, this try-block is to verify the web token stored in the session object after the 
                                // user has login and verify using the JWTUtility with the sharedSecret
                                token = item.getString();
                                if (token != null) {
                                    if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    bootstrapResult = new LinkedHashMap<String, Object>();
                    //Declare and initialize messages to store any form of error messages
                    ArrayList<String> messages = new ArrayList<String>();

                    if (token == null) {
                        messages.add(ErrorMessage.getMsg("missingToken"));
                    } else if (token.isEmpty()) {
                        messages.add(ErrorMessage.getMsg("blankToken"));
                    } else {
                        if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                            messages.add(ErrorMessage.getMsg("invalidToken"));
                            //If this is a user
                            if (sourcePage != null && sourcePage.equals("bootstrap.jsp")) {
                                response.sendRedirect("login.jsp");
                                return;
                            }
                        }
                    }

                    //If messages size is bigger than 0, means there is an error, and we should display error messages and do not process the file
                    if (messages.size() > 0) {
                        bootstrapResult.put("status", "error");
                        bootstrapResult.put("messages", messages);
                    } else {

                        // looping through the itemlist to find what you want 
                        for (FileItem item : items) {

                            // if item is formfield means that it is NOT a file and not formfield means it is a file
                            if (!item.isFormField()) {

                                // retrieve the name of the item /file uploaded (e.g. bootstrap.zip)
                                String fileName = item.getName();

                                // designate a path for the file to be uploaded, at the moment file is empty
                                File file = new File(repository.getAbsolutePath() + File.separator + fileName);

                                // write the content to the file
                                item.write(file);

                                // instantiate the processFileController and to process the file
                                ProcessFileController processFileController = new ProcessFileController();
                                bootstrapResult = processFileController.processFile(file);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            // Instatiate writer
            PrintWriter out = response.getWriter();

            // Instantiate JsonPrint utility
            JsonPrint jsonPrint = new JsonPrint();

            // Get JSON result String
            String prettyJsonString = jsonPrint.prettyPrint(bootstrapResult);

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
