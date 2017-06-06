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
<%@include file="process_upload.jsp" %>
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
                            <button class="button radius secondary small">
                                <i class="fa fa-user-times" aria-hidden="true"></i>
                                Details
                            </button>
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
                <form action="unassignManagerServlet" method="POST">
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
                                <input type="submit" class="button radius primary small" value="Unassign" >
                            <button class="button radius secondary small">
                                <i class="fa fa-user-times" aria-hidden="true"></i>
                                Details
                            </button>
                        </div>
                    </div>
                </div>
                </form>
                <%
                    }
                %>
            </div>
        </div>
        <%
            }
        %>

        <!-- **************************** End Manager Result ********************-->


        <!--
        <div class="row add-people-section">
            <div class="small-12 medium-6 columns about-people">
                <div class="about-people-avatar">
                    <img class="avatar-image" src="https://i.imgur.com/GHeazQ2.jpg" alt="Kishore Kumar">
                </div>
                <div class="about-people-author">
                    <p class="author-name">
                        Barack Obama
                    </p>
                    <p class="author-location">
                        <i class="fa fa-map-marker" aria-hidden="true"></i>
                        Hawaii, United States
                    </p>
                    <p class="author-mutual">
                        <strong>Hilary Clinton</strong> is a mutual friend.
                    </p>
                </div>    
            </div>
            <div class="small-12 medium-6 columns add-friend">
                <div class="add-friend-action">
                    <button class="button primary small">
                        <i class="fa fa-user-plus" aria-hidden="true"></i>
                        Unassign
                    </button>
                    <button class="button secondary small">
                        <i class="fa fa-user-times" aria-hidden="true"></i>
                        Details
                    </button>
                </div>
            </div>
        </div>
        <div class="row add-people-section">
            <div class="small-12 medium-6 columns about-people">
                <div class="about-people-avatar">
                    <img class="avatar-image" src="https://i.imgur.com/SytPzuC.jpg" alt="Kishore Kumar">
                </div>
                <div class="about-people-author">
                    <p class="author-name">
                        Harry Manchanda
                    </p>
                    <p class="author-location">
                        <i class="fa fa-map-marker" aria-hidden="true"></i>
                        New Delhi, India
                    </p>
                    <p class="author-mutual">
                        <strong>Rafi Benkual</strong> is a mutual friend.
                    </p>
                </div>    
            </div>
            <div class="small-12 medium-6 columns add-friend">
                <div class="add-friend-action">
                    <button class="button primary small">
                        <i class="fa fa-user-plus" aria-hidden="true"></i>
                        Unassign
                    </button>
                    <button class="button secondary small">
                        <i class="fa fa-user-times" aria-hidden="true"></i>
                        Details
                    </button>
                </div>
            </div>
        </div>
        <!--<div class="view-more-people">
            <p class="view-more-text">
                <a href="#" class="view-more-link">View More..</a>
            </p>
        </div>-->


        <%        if (fileError != null) {
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
                                        LinkedHashMap<String, Object> num = numList.get(n);
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
                                        LinkedHashMap<String, Object> err = errorList.get(n);
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