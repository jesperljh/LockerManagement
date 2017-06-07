<%-- 
    Document   : AssignLockerToManager
    Created on : Jun 6, 2017, 2:41:28 PM
    Author     : Jesper
--%>
<%@page import="controller.DemographicsCSVController"%>
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
    } else if (currentUser != null && !currentUser.getRole().equals("admin")) {
        response.sendRedirect("login.jsp");
        return;
    }
    String fileError = null;
%>
<!DOCTYPE html>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JPIntern | Locker Assignment</title>
        <link rel="stylesheet" href="css/foundation.css" />
        <script src="js/vendor/modernizr.js"></script>
        <!--Icon css-->
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">
        <!-- seat and people css -->
        <link rel="stylesheet" href="css/seat.css" />
    </head> 
    <% 
        String sid = request.getParameter("sid");
        if(sid == null){
            response.sendRedirect("bootstrap.jsp");
        }
        DemographicsCSVController demographicController = new DemographicsCSVController();
        Demographics manager = demographicController.getUser(sid);
        %>
    <body>
        <%@include file="include/admintopbar.jsp" %>
        <!--Page Header-->
        <h1>jerome</h1>
        <div class="row" style="padding-top: 50px">
            <!--Icon-->
            <i class="fi-upload size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspAssign Locker Cluster to <%=manager.getNeighbourhood().toUpperCase() %> Manager</strong></h3>
            <!--Divider-->
            <hr>

            <form method="POST">
                <!-- Date Time Picker -->
                <label><strong>SID</strong>
                    <input class="radius" style="width: 250px" name="sid" type="text" placeholder="Enter SID (S123456)" id="sid"/> 
                </label>
                <input type="submit" value="Search" class="button sloca normal radius"/>
            </form>
        </div>
        
        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/foundation.min.js"></script>
        <script>
            $(document).foundation();
        </script>
    </body>
</html>