<%-- 
    Document   : admin.jsp
    Created on : Sep 11, 2014, 3:14:29 PM
    Author     : Jesper Lim
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
    } else if (currentUser != null && !currentUser.getRole().equals("manager")) {
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
    <body>
        <%@include file="include/topbar.jsp" %>
        <!--Page Header-->
        <div class="row" style="padding-top: 50px">
            <!--Icon-->
            <i class="fi-upload size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspRegister User</strong></h3>
            <!--Divider-->
            <hr>
            <%                String sid;
                if (request.getMethod() != null && request.getMethod().equals("POST")) {
                    //Get the user input
                    sid = request.getParameter("sid");
                    DemographicsCSVController demoCtrl = new DemographicsCSVController();
                    Demographics demo = demoCtrl.getUser(sid);
                    if (demo == null) {
            %>
            <div data-alert class="alert-box round" style="background-color: #5e001f">
                User not found - Please enter correct SID.
                <a href="#" class="close" style="color: whitesmoke; font-size: 25px">&times;</a>
            </div>
            <%
                    }
                }
            %>
            <form method="POST">
                <!-- Date Time Picker -->
                <label><strong>SID</strong>
                    <input class="radius" style="width: 250px" name="sid" type="text" placeholder="Enter SID (S123456)" id="sid"/> 
                </label>
                <input type="submit" value="Search" class="button sloca normal radius"/>
            </form>
        </div>

        <!-- **************************** Search SID Result ********************-->
        <%
            if (request.getMethod() != null && request.getMethod().equals("POST")) {
                //Get the user input
                sid = request.getParameter("sid");
                DemographicsCSVController demoCtrl = new DemographicsCSVController();
                Demographics demo = demoCtrl.getUser(sid);
                if (demo != null) {

        %>
        <div class="row">
            <div class="people-you-might-know">
                <div class="add-people-header">
                    <h6 class="header-title">
                        Search Result
                    </h6>
                </div>

                <% if (demo.getRole() != null && demo.getRole().equals("user")) {
                %>
                <form action="registerUserServlet" method="POST">
                    <div class="row add-people-section">
                        <div class="small-12 medium-6 columns about-people">
                            <div class="about-people-avatar">
                                <img class="avatar-image" src="https://i.imgur.com/UPVxPjb.jpg" alt="Kishore Kumar">
                            </div>
                            <div class="about-people-author">

                                <p class="author-name" id="user_name">                            
                                    <%= demo.getName()%>                               
                                </p>
                                <p class="author-mutual" id="user_sid">
                                    <strong><%= demo.getSid()%></strong>
                                </p>
                            </div>    
                        </div>
                        <div class="small-12 medium-6 columns add-friend">
                            <div class="add-friend-action">
                                <input type="hidden" name="user_sid" id="user_sid" value="<%=demo.getSid()%>">
                                <input type="hidden" name="mgr_sid" id="mgr_sid" value="<%=currentUser.getSid()%>">
                                <input type="submit" class="radius button small" value="Add to Neighbourhood">   
                                <input type="hidden" name="nb" value="<%=currentUser.getNeighbourhood()%>">                            
                            </div>
                        </div>
                    </div>
                </form>
                <% } else { %>
                <script>alert('SID belongs to a manager');</script>
                <p class="author-mutual"> No users found</p>
                <% } %>
            </div>
        </div>    
        <%
                } // End of if(demo != null())
            } // end of if (request.getMethod() != null && request.getMethod().equals("POST")) {
        %>                    

        <!-- **************************** End Search SID Result ********************-->


        <!-- **************************** User Result ********************-->
        <%
            DemographicsCSVController demoController = new DemographicsCSVController();
            ArrayList<Demographics> demoList = demoController.getUsersByNeighbourHood(currentUser.getNeighbourhood());
            if (demoList != null && !demoList.isEmpty()) {
        %>
        <div class="row" style="padding-top: 50px">
            <div class="people-you-might-know">
                <div class="add-people-header">
                    <h6 class="header-title">
                        Users in your Neighbourhood
                    </h6>
                </div>
                <%
                    for (Demographics demographics : demoList) {
                %>
                <div class="row add-people-section">
                    <div class="small-12 medium-6 columns about-people">
                        <div class="about-people-avatar">
                            <img class="avatar-image" src="https://i.imgur.com/UPVxPjb.jpg" alt="Kishore Kumar">
                        </div>
                        <div class="about-people-author">
                            <p class="author-name">
                                <%= demographics.getName()%>
                            </p>
                            <p class="author-location">
                                <i class="fa fa-map-marker" aria-hidden="true"></i>
                                <%= demographics.getNeighbourhood().toUpperCase() + " " + demographics.getRole()%>
                            </p>
                            <p class="author-mutual">
                                <strong><%= demographics.getSid()%></strong>
                                <input type="hidden" name="sid" id="sid" value="<%= demographics.getSid()%>">
                                <input type="hidden" name="neighbourhood" id="neighbourhood" value="<%= demographics.getNeighbourhood()%>">
                                <input type="hidden" name="role" id="role" value="user">
                            </p>
                        </div>    
                    </div>                                 
                </div>
                <%
                    }
                %>
            </div>
        </div>
        <%
            }
        %>

        <!-- **************************** End User Result ********************-->
        <br/>

        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/foundation.min.js"></script>
        <script>
                    $(document).foundation();
        </script>
    </body>
</html>
