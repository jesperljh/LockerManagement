<%-- 
    Document   : process_upload.jsp
    Created on : Oct 5, 2014, 4:32:23 PM
    Author     : jxsim.2013
--%>
<%@page import="controller.ProcessFileController"%>
<%@page import="is203.JWTException"%>
<%@page import="utility.SharedKey"%>
<%@page import="is203.JWTUtility"%>
<%@page import="org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@page import="java.util.List"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>
<%
    String sourcePage = null;
    String token = null;
    LinkedHashMap<String,Object> uploadResult = null;

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
                            try {
                                token = item.getString();
                                if (token != null) {
                                    String isVerified = JWTUtility.verify(token, SharedKey.getKey());

                                    // token is wrong 
                                    if (!isVerified.equals("admin")) {
                                        response.sendRedirect("login.jsp");
                                        return;

                                    }
                                }
                            } catch (JWTException ex) {
                                response.sendRedirect("login.jsp");
                                return;
                            }

                        }
                    }
                }

                //Let's check if token is still null
                if (token == null) {
                    //If this is a user
                    if (sourcePage != null && sourcePage.equals("bootstrap.jsp")) {
                        response.sendRedirect("login.jsp");
                        return;
                    } else {
                        //If this is a pure json request
                        return;
                    }
                }

                // looping through the itemlist to find what you want 
                for (FileItem item : items) {

                    // if item is formfield means that it is NOT a file and not formfield means it is a file
                    if (!item.isFormField()) {

                        // retrieve the name of the item /file uploaded (e.g. bootstrap.zip)
                        String fileName = item.getName();

                        if (fileName.endsWith(".csv") && sourcePage.equals("uploadcsv.jsp")) {
                            fileError = "error";
                        }
                        if (fileName.endsWith(".csv") && sourcePage.equals("bootstrap.jsp")) {
                            fileError = "error";
                        }

                        if (fileError == null) {
                            // designate a path for the file to be uploaded, at the moment file is empty
                            File file = new File(repository.getAbsolutePath() + File.separator + fileName);

                            // write the content to the file
                            item.write(file);

                            // instantiate the uploadController and to process the file
                            ProcessFileController uploadController = new ProcessFileController();
                            uploadResult = uploadController.processFile(file);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
%>