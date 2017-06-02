<%--
    Document   : breakdown
    Created on : Oct 2, 2014, 12:39:51 PM
    Author     : Jiacheng
--%>

<%@page import="utility.ErrorMessage"%>
<%@page import="utility.StringFormat"%>
<%@page import="java.lang.Object"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="controller.BasicLocReportController"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<%    // Declare a LinkedHashMap to store the basicLocationResult
    LinkedHashMap<String, Object> basicLocationResult = null;

    // Declare a dateTime to store the user input (date time)
    String dateTime = "";

    // Declare an array of fixed number of strings to store the selected breakdowns
    String[] order = new String[3];

    // Declare an integer to indicate the breakdown level = 0
    int breakdownLevel = 0;
    
    String firstBreakdown = "";
    String secondBreakdown = "";
    String thirdBreakdown = "";

    // To check if it is a post form
    if (request.getMethod() != null && request.getMethod().equals("POST")) {

        // To retrieve the dateTime from the form textbox 
        dateTime = request.getParameter("dateTime").replace(' ', 'T');

        // To retrieve the specfic breakdown ( /Y/G/S) from the dropdown list
        firstBreakdown = request.getParameter("firstBreakdown");
        secondBreakdown = request.getParameter("secondBreakdown");
        thirdBreakdown = request.getParameter("thirdBreakdown");
        if (!firstBreakdown.isEmpty() && secondBreakdown.isEmpty() && thirdBreakdown.isEmpty()) {
            breakdownLevel = 1;
            order = new String[1];
            order[0] = firstBreakdown;
        } else if (!firstBreakdown.isEmpty() && !secondBreakdown.isEmpty() && thirdBreakdown.isEmpty()) {
            breakdownLevel = 2;
            order = new String[2];
            order[0] = firstBreakdown;
            order[1] = secondBreakdown;
        } else if (!firstBreakdown.isEmpty() && !secondBreakdown.isEmpty() && !thirdBreakdown.isEmpty()) {
            breakdownLevel = 3;
            order = new String[3];
            order[0] = firstBreakdown;
            order[1] = secondBreakdown;
            order[2] = thirdBreakdown;
        }
        if (!firstBreakdown.isEmpty() && secondBreakdown.isEmpty() && !thirdBreakdown.isEmpty()) {
            basicLocationResult = new LinkedHashMap<String, Object>();
            basicLocationResult.put("status", "error");
            ArrayList<String> messages = new ArrayList<String>();
            messages.add("Second breakdown cannot be blank");
            basicLocationResult.put("messages", messages);
        } else if (firstBreakdown.equals(secondBreakdown) || firstBreakdown.equals(thirdBreakdown) || (!secondBreakdown.isEmpty() && !thirdBreakdown.isEmpty() && secondBreakdown.equals(thirdBreakdown))) {
            basicLocationResult = new LinkedHashMap<String, Object>();
            basicLocationResult.put("status", "error");
            ArrayList<String> messages = new ArrayList<String>();
            messages.add("Breakdown options cannot be repeated");
            basicLocationResult.put("messages", messages);
        } else {

            // Declare and initialize the basicLocReportController
            BasicLocReportController basicLocReportController = new BasicLocReportController();
            // Call the getBasicLocReport from the basicLocReportController with the validated parameter specified by the user
            // Store the basicLocReport into the LinkedHashMap 
            String token = (String) session.getAttribute("token");
            basicLocationResult = basicLocReportController.getBasicLocReport(dateTime, order, token, false);
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Basic Location Report | Breakdown by Gender, Year, School</title>
        <link rel="stylesheet" href="css/foundation.css" />
        <script src="js/vendor/modernizr.js"></script>

        <!--Icon css-->
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">

        <!-- DatePicker css-->
        <link rel="stylesheet" href="css/datetime/jquery.datetimepicker.css">
    </head>
    <body>
        <%@include file="include/topbar.jsp" %>
        <!--Page Header-->
        <div class="row" style="padding-top: 30px; padding-left: 18px; padding-right: 18px">
            <!--Icon-->
            <i class="fi-results size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspBreakdown by Gender, Year, School</strong></h3>
            <!--Divider-->
            <hr/>
        </div>

        <div class="row">
            <div class="small-4 columns">
                <%@include file="include/includeDatetime.jsp" %>

                <!-- To display results -->
                <%
                    // To check if it pass the POST method. 
                    // If is not null, means it is a POST which contains results
                    if (basicLocationResult != null) {

                        // Retrieve the error messagesList from the basicLocationResult
                        ArrayList<String> messagesList = (ArrayList) basicLocationResult.get("messages");

                        ArrayList<LinkedHashMap<String, Object>> firstList = (ArrayList) basicLocationResult.get("breakdown");

                        // To check if there is any error messages. If have error messages, display in the page
                        if (messagesList != null && messagesList.size() > 0) {

                            // Loop the message list and retrieve each error messages
                            for (String message : messagesList) {
                %> 
                <div data-alert class="alert-box alert radius" align="center">
                    <!-- Display the error message -->
                    <%=message%>
                </div>
                <%
                    }
                } else {
                    // There is no error messages.           
                %>
                <!-- Notification for Displaying Results -->
                <div data-alert class="alert-box success radius" align="center">
                    Results Displayed
                </div>
                <!-- Display Results -->
                <%                }
                    }
                %>

                <!-- Form -->
                <!-- Date & Time Form -->
                <form action="" method="POST">
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <!-- To remove the 'T' after user posted (do not want to show a 'T' there)-->
                        <input name="dateTime" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace('T', ' ')%>" required/> 
                    </label>

                    <!-- Break Down Order Form -->
                    <!-- First Breakdown -->
                    <label><strong>1<sup>st</sup> Breakdown<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <select name="firstBreakdown" required>
                            <option value="">Select a Filter</option>
                            <option value="gender" <%
                                // To print the previously selected breakdown
                                if (firstBreakdown.equals("gender")) {
                                    out.print("selected");
                                }%>>Gender</option> 
                            <option value="school" <%
                                if (firstBreakdown.equals("school")) {
                                    out.print("selected");
                                }%>>School</option> 
                            <option value="year" <%
                                if (firstBreakdown.equals("year")) {
                                    out.print("selected");
                                }%>>Year</option>
                        </select>
                    </label>

                    <!-- Second Breakdown -->
                    <label><strong>2<sup>nd</sup> Breakdown</strong>
                        <select name="secondBreakdown">
                            <option value="">Select a Filter</option>
                            <option value="gender" <% if (secondBreakdown.equals("gender")) {
                                    out.print("selected");
                                }%>>Gender</option> 
                            <option value="school" <% if (secondBreakdown.equals("school")) {
                                    out.print("selected");
                                }%>>School</option> 
                            <option value="year" <% if (secondBreakdown.equals("year")) {
                                    out.print("selected");
                                }%>>Year</option>
                        </select>
                    </label>

                    <!-- Third Breakdown -->
                    <label><strong>3<sup>rd</sup> Breakdown</strong>
                        <select name="thirdBreakdown">
                            <option value="">Select a Filter</option>
                            <option value="gender" <% if (thirdBreakdown.equals("gender")) {
                                    out.print("selected");
                                }%>>Gender</option> 
                            <option value="school" <% if (thirdBreakdown.equals("school")) {
                                    out.print("selected");
                                }%>>School</option> 
                            <option value="year" <% if (thirdBreakdown.equals("year")) {
                                    out.print("selected");
                                }%>>Year</option>
                        </select>
                    </label>

                    <!-- Submit Button -->
                    <input type="submit" value="Submit" class="button sloca normal radius"/>
                </form>
            </div>
            <!-- Displaying Results -->

            <div class="small-8 columns">
                <%

                    if (basicLocationResult != null) {
                        ArrayList<String> messages = (ArrayList<String>) basicLocationResult.get("messages");
                        if (messages == null) {
                %>
                <table>
                    <thead>
                    <th>Queried Users</th>
                    <th>Count</th>
                    </thead>
                    <tbody>
                        <tr>
                            <td><center>Total Users</center></td>
                    <td><center><%=basicLocationResult.get("totalDemographics")%></center></td>
                    </tr>
                    </tbody>
                </table>
                <%
                    if (breakdownLevel == 1) {
            // If breakdown level is one, include breakdownTableOne.jsp to display results
                %>
                <%@include file="include\breakdownTableOne.jsp"%>
                <%
                } // If breakdown level is two, include breakdownTableOne.jsp to display results
                else if (breakdownLevel == 2) {
                %>
                <%@include file="include\breakdownTableTwo.jsp"%>
                <%
                } // If breakdown level is three, include breakdownTableOne.jsp to display results
                else if (breakdownLevel == 3) {
                %>
                <%@include file="include\breakdownTableThree.jsp"%>
                <%
                            }
                        }
                    }
                %>
            </div>
        </div>


        <!-- Included JS Files (Compressed) -->
        <script src="js/datetime/foundation.min.js" type="text/javascript"></script>

        <!-- this is the javascript for datetimepicker -->
        <script src="js/datetime/jquery.datetimepicker.js" type="text/javascript"></script>
        <!-- end of the javascript for datetimepicker -->

        <!-- Initialize JS Plugins -->
        <script src="js/datetime/app.js" type="text/javascript"></script>

        <script src="js/vendor/jquery.js"></script>
        <script>
            $(document).foundation();
        </script>
    </body>
</html>