<%-- 
    Document   : bootstrap.jsp
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
    <body>
        <%@include file="include/admintopbar.jsp" %>
        <!--Page Header-->
        <div class="row" style="padding-top: 50px">
            <!--Icon-->
            <i class="fi-upload size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspNeighbourhood Manager</strong></h3>
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

        <!-- **************************** Search SID Result ********************-->
        <%            String sid;
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
                <div class="row add-people-section">
                    <div class="small-12 medium-6 columns about-people">
                        <div class="about-people-avatar">
                            <img class="avatar-image" src="https://i.imgur.com/UPVxPjb.jpg" alt="Kishore Kumar">
                        </div>
                        <div class="about-people-author">
                            <p class="author-name">
                                <%= demo.getName()%>
                            </p>
                            <p class="author-location">
                                <i class="fa fa-map-marker" aria-hidden="true"></i>
                                <%= demo.getRole()%>
                            </p>
                            <p class="author-mutual">
                                <strong><%= demo.getSid()%></strong>
                            </p>
                        </div>    
                    </div>
                    <div class="small-12 medium-6 columns add-friend">
                        <div class="add-friend-action">
                            <button data-reveal-id="firstModal" class="radius button small">
                                <i class="fa fa-user-plus" aria-hidden="true"></i>
                                Assign Manager
                            </button>
                            <a href="/AssignLockerToManager?sid=<%=demo.getSid()%>" class="button radius secondary small">Assign Locker Cluster</a>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Reveal Modals begin -->
        <div id="firstModal" class="reveal-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
            <h2 id="firstModalTitle">Please choose neighbourhood</h2>
            <form action="assignManagerServlet" method="POST">
                <input type="hidden" name="searchSID" id="searchSID" value="<%= demo.getSid()%>">
                <input type="hidden" name="role" id="role" value="manager">
                <label><strong>Choose Neigbourhood</strong>
                    <select name="neighbourhood" id="neighbourhood" required>
                        <%
                            DemographicsCSVController dController = new DemographicsCSVController();
                            ArrayList<Demographics> demoList = dController.getManagers();
                            if (demoList != null && !demoList.isEmpty()) {
                                ArrayList<String> a = new ArrayList<String>();
                                for (Demographics d : demoList) {
                                    a.add(d.getNeighbourhood());
                                }
                                if (!a.contains("batman")) {
                        %>
                        <option value="batman" selected>Batman Neighbourhood</option> 
                        <%
                            }
                            if (!a.contains("spidarman")) {
                        %>
                        <option value="spidarman">Spidarman Neighbourhood</option>
                        <%
                            }
                            if (!a.contains("superman")) {
                        %>
                        <option value="superman">Superman Neighbourhood</option> 
                        <%
                            }
                            if (!a.contains("ironman")) {
                        %>
                        <option value="ironman">Ironman Neighbourhood</option> 
                        <%
                                }
                            }
                        %>
                    </select>
                </label>
                <p><input type="submit" class="button sloca normal "value="Assign" /></p>
                <a class="close-reveal-modal" aria-label="Close">&#215;</a>
            </form>
        </div>
        <%
                }
            }
        %>

        <!-- **************************** End Search SID Result ********************-->


        <!-- **************************** Manager Result ********************-->
        <%
            DemographicsCSVController demoController = new DemographicsCSVController();
            ArrayList<Demographics> demoList = demoController.getManagers();
            if (demoList != null && !demoList.isEmpty()) {
        %>
        <div class="row" style="padding-top: 50px">
            <div class="people-you-might-know">
                <div class="add-people-header">
                    <h6 class="header-title">
                        People you have assigned as Neighbourhood Manager
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
                        <div class="small-12 medium-6 columns add-friend">
                            <div class="add-friend-action">
                                <button data-reveal-id="<%=demographics.getSid()%>Modal" class="button radius primary small">
                                    <i class="fa fa-user-plus" aria-hidden="true"></i>
                                    Unassign Manager
                                </button>
                                <a href="/LockerAssignment/AssignLockerToManager.jsp?sid=<%=demographics.getSid()%>" class="button radius secondary small">Assign Locker Cluster</a>
                            </div>
                        </div>
                    </div>

                    <!-- Reveal Modals begin -->
                    <div id="<%=demographics.getSid()%>Modal" class="reveal-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
                        <h2 id="firstModalTitle">Remove all users assigned to this neighbourhood?</h2>
                        <form action="unassignManagerServlet" method="POST">
                            <input type="hidden" name="sid" id="sid" value="<%=demographics.getSid()%>">
                            <input type="hidden" name="neighbourhood" id="neighbourhood" value="<%=demographics.getNeighbourhood()%>">
                            <label><strong>Please choose:</strong></label>
                            <input type="radio" name="choice" value="yes" checked> Yes<br>
                            <input type="radio" name="choice" value="no"> No<br>  
                            <p><input type="submit" class="button sloca normal" value="Submit" /></p>
                            <a class="close-reveal-modal" aria-label="Close">&#215;</a>
                        </form>
                    </div>
                <%
                    }
                %>
            </div>
        </div>
        <%
            }
        %>

        <!-- **************************** End Manager Result ********************-->
        <br/>

        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/foundation.min.js"></script>
        <script>
            $(document).foundation();
        </script>
    </body>
</html>
