<%-- 
    Document   : automaticgroupdetection
    Created on : Oct 19, 2014, 1:41:26 PM
    Author     : Kenneth/Jingxiang
--%>

<%@page import="controller.AutomaticGroupDetectionController"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="entity.Group"%>
<%@page import="utility.ErrorMessage"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="controller.HeatmapController"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<%    //Declare HashMap to store heatmap results 
    LinkedHashMap<String, Object> groupResults = null;

    //Declare variables
    String dateTime = "";
    String token = (String) session.getAttribute("token");
    //if this is a post
    if (request.getMethod() != null && request.getMethod().equals("POST")) {

        //This replaces the ' ' with a T as required by the controller.
        dateTime = request.getParameter("date").replace(" ", "T");

        //This stores the datetime for transferring between from page to page.
        session.setAttribute("datetimeInput", dateTime);

        AutomaticGroupDetectionController agdc = new AutomaticGroupDetectionController();
        groupResults = agdc.getAutomaticGroupDetection(dateTime, token, false, false);

    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Automatic Group Detection</title>

        <!-- Google Fonts -->
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>

        <!-- Zurb Foundations CSS -->
        <link rel="stylesheet" href="css/foundation.css" />
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">

        <!-- DatePicker CSS & Javascript-->
        <link rel="stylesheet" href="css/datetime/jquery.datetimepicker.css">
        <script src="js/vendor/modernizr.js"></script>

        <!-- SortTable -->
        <script src="js/sorttable.js"></script>
    </head>
    <body>
        <%@include file="include/topbar.jsp" %>

        <!--Page Header-->
        <div class="row" style="padding-top: 30px; padding-left: 18px; padding-right: 18px">
            <i class="fi-magnifying-glass size-48"></i>
            <h3 style="display: inline-block;"><strong>&nbspAutomatic Group Detection</strong></h3>
            <!--Divider-->
            <hr>
        </div>

        <div class="row">
            <div class="small-4 columns">
                <%@include file="include/includeDatetime.jsp" %>
                <%
                    if (groupResults != null) {
                        //If this is not null, then this is a POST and results is stored
                        //Get the list of messagesList, this contains error
                        ArrayList<String> messages = (ArrayList) groupResults.get("messages");

                        ArrayList<HashMap> groupList = (ArrayList) groupResults.get("groups");

                        //If there are errors :(
                        if (messages != null && messages.size() > 0) {

                            //For loop to iterate them out
                            for (String message : messages) {
                %> 
                <div data-alert class="alert-box alert radius" align="center">
                    <%=message%>
                </div>
                <%
                    }
                } else if (groupList.size() == 0) {
                %>
                <div data-alert class="alert-box alert radius" align="center">
                    <%=ErrorMessage.getMsg("noResultsFound")%>
                </div>
                <%
                } else {
                %>
                <div data-alert class="alert-box success radius" align="center">
                    Results Displayed

                </div>
                <%                }
                    }
                %>
                <form action="" method="POST">
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input name="date" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace("T", " ")%>" required/> 
                    </label>

                    <!--Submit-->
                    <input type="submit" value="Submit" class="button sloca normal radius"/>
                </form>
            </div>
            <div class="small-8 columns">
                <%
                    //There are results

                    if (groupResults != null) {
                        ArrayList<LinkedHashMap> groupList = (ArrayList) groupResults.get("groups");
                        String totalUsers = groupResults.get("total-users").toString();
                        String totalGroups = groupResults.get("total-groups").toString();
                %>
                <table>
                    <thead>
                    <th>Queried Users</th>
                    <th>Count</th>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Number of Users in SIS</td>
                            <td><%=totalUsers%></td>
                        </tr>
                        <tr>
                            <td>Total Groups</td>
                            <td><%=totalGroups%></td>
                        </tr>
                    </tbody>

                    <%
                        if (groupList != null && groupList.size() > 0) {
                            for (LinkedHashMap<String, Object> g : groupList) {
                                ArrayList<LinkedHashMap<String, Object>> membersList = (ArrayList) g.get("members");
                                ArrayList<LinkedHashMap<String, Object>> locationsList = (ArrayList) g.get("locations");
                    %> 
                    <table>
                        <thead style="background-color: red">
                            <tr>
                                <th>
                        <center>
                            Group Size: <%=g.get("size")%> users
                        </center>
                        </th>                    
                        </tr>
                        <tr>
                            <th>
                                Total Time Spent: <%= g.get("total-time-spent")%> seconds 
                            </th>                    
                        </tr>
                        </thead>
                    </table>
                    <table>
                        <thead>
                            <tr>
                                <th style="width:46%"><center>Email</center></th>
                        <th style="width:54%"><center>Mac Address</center></th>
                        </tr>
                        </thead>
                        <tbody>
                            <%
                                for (LinkedHashMap<String, Object> member : membersList) {
                            %>
                            <tr>
                                <td style="word-wrap: break-word">
                                    <%= member.get("email")%>
                                </td>
                                <td style="word-wrap: break-word">
                                    <%= member.get("mac-address")%>
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <table>
                        <thead>
                            <tr>
                                <th>Location</th>
                                <th>Time Spent</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (LinkedHashMap<String, Object> location : locationsList) {
                            %>
                            <tr>
                                <td>
                        <center>
                            <%= location.get("location")%>
                        </center>
                        </td>
                        <td>
                        <center>
                            <%= location.get("time-spent")%>
                        </center>
                        </td>
                        </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                    <%
                                }
                            }
                        }
                    %>
                    </div>
                    </div>



                    <!-- Included JS Files (Compressed) -->
                    <script src="js/vendor/jquery.js"></script>
                    <script src="js/datetime/foundation.min.js" type="text/javascript"></script>

                    <!-- this is the javascript for datetimepicker -->
                    <script src="js/datetime/jquery.datetimepicker.js" type="text/javascript"></script>
                    <!-- end of the javascript for datetimepicker -->

                    <!-- Initialize JS Plugins -->
                    <script src="js/datetime/app.js" type="text/javascript"></script>

                    <script>
                        $(document).foundation();
                    </script>
                    </body>
                    </html>
