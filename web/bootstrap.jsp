<%-- 
    Document   : bootstrap.jsp
    Created on : Sep 11, 2014, 3:14:29 PM
    Author     : Eugene/Kenneth
--%>
<%@page import="java.util.Map"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Demographics currentUser = (Demographics) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    } else if (currentUser != null && !currentUser.getEmail().equals("admin")) {
        response.sendRedirect("login.jsp");
        return;
    }
    String fileError = null;
%>
<%@include file="process_upload.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Bootstrap</title>
        <link rel="stylesheet" href="css/foundation.css" />
        <script src="js/vendor/modernizr.js"></script>
                <!--Icon css-->
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">

    </head> 
    <body>
        <%@include file="include/admintopbar.jsp" %>
       <!--Page Header-->
        <div class="row" style="padding-top: 50px">
            <!--Icon-->
            <i class="fi-upload size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspBootstrap</strong></h3>
            <!--Divider-->
            <hr>
        </div>

    <%
        if (fileError != null) {
    %>
    <div class="row">
        <div class="large-12">
            <div class="row">
                <div class="large-5 large-centered columns">
                    <div data-alert class="alert-box alert">
                        The file uploaded must be a .zip file. Please try again.
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%        }
    %>
    <br/>
    <div class="row"> 
        <div class="large-12 columns">
            <%
                LinkedHashMap<String, Object> bootstrapResult = uploadResult;
        
                //Show the success message
                if (bootstrapResult != null && fileError == null) {
                    %>
                        <div class="row">
                            <div class="large-4 large-centered columns">
                                <div data-alert class="alert-box success" align="center">
                                    Bootstrap Results Displayed
                                    
                                </div>
                            </div>
                        </div>                     
                    <%
                }
            %>
            <form method="post" action="" enctype="multipart/form-data">
                <label>Select Zip File
                    <input type="hidden" name="page" value="bootstrap.jsp">
                    <input type="hidden" name="token" value="<%=session.getAttribute("token")%>">
                    <input type="file" name="bootstrap" accept="application/zip" required/>
                    <input type="submit" class="button sloca normal" value="Bootstrap" />
                </label>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="large-10 large-centered columns">
            <%
                if (bootstrapResult != null) {
            %>

            <table>
                <tr>
                    <td style="width: 200px">Status</td>
                    <td><%= (String) bootstrapResult.get("status")%></td>
                </tr>
                <tr>
                    <td style="width: 200px">Num-Record-Loaded</td>
                    <td>
                        <%
                            ArrayList<LinkedHashMap<String, Object>> numList = (ArrayList<LinkedHashMap<String, Object>>) bootstrapResult.get("num-record-loaded");
                            if (numList != null) {
                                for (int n = 0; n < numList.size(); n++) {
                                    LinkedHashMap<String,Object> num = numList.get(n);
                                    for (Map.Entry<String, Object> obj : num.entrySet()) {
                                        out.println(obj.getKey() + " - Lines: " + obj.getValue() + "<br>");
                                    }
                                }
                            }
                        %>
                    </td>
                </tr>
                <tr>
                    <td style="width: 200px">Error</td>
                    <td>
                        <%
                            ArrayList<LinkedHashMap<String, Object>> errorList = (ArrayList<LinkedHashMap<String, Object>>) bootstrapResult.get("error");
                            if (errorList != null) {
                                for (int n = 0; n < errorList.size(); n++) {
                                    LinkedHashMap<String,Object> err = errorList.get(n);
                                    for (Map.Entry<String, Object> obj : err.entrySet()) {
                                        out.println(obj.getKey() + " -  " + obj.getValue());
                                    }
                                    out.println("<br>");
                                }
                            }
                        %>
                    </td>
                </tr>
            </table>
            <%

                }
            %>
        </div>
    </div>
    <!-- Included JS Files (Compressed) -->
    <script src="js/vendor/jquery.js"></script>
    <script src="js/foundation.min.js"></script>
    <script>
        $(document).foundation();
    </script>
</body>
</html>
