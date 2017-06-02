<%-- 
    Document   : topknextplaces
    Created on : Oct 13, 2014, 1:19:12 PM
    Author     : Jocelyn
--%>

<%@page import="controller.TopKNextPlacesController"%>
<%@page import="utility.ErrorMessage"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="entity.LocationLookup"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dao.LocationLookupDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<!DOCTYPE html>
<%    // Initialize k as a empty String
    String k = "";

    // Initialize dateTime as a empty String
    String dateTime = "";

    // Initialize semanticPlace as a empty String
    String semanticPlace = "";

    //retrieve of token for validation
    String token = (String) session.getAttribute("token");

    // Initialize TopKNextPlacesController as null
    LinkedHashMap<String, Object> topKNextPlacesResults = null;

    if (request.getMethod() != null && request.getMethod().equals("POST")) {

        // Instantiating locationLookupDAO
        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();

        // Instantiating TopKNextPlacesController
        TopKNextPlacesController topKNextPlacesController = new TopKNextPlacesController();

        // This replaces the ' ' with a T as required by the controller
        dateTime = request.getParameter("date").replace(' ', 'T');

        // Retrieve the k value that user has selected
        k = request.getParameter("k");

        // Retrieve the semanticPlace value that user has selected
        semanticPlace = request.getParameter("semanticPlace");

        // Retrieve and Instantiate the results from TopKPopularPlaceController
        topKNextPlacesResults = topKNextPlacesController.getTopKNextPlaces(semanticPlace, dateTime, k, token, false);
    }
%>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Basic Location Report | Top-K Next Places</title>
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
            <i class="fi-marker size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspTop-K Next Places</strong></h3>
            <!--Divider-->
            <hr>
        </div>

        <div class="row">
            <div class="small-4 columns">
                <%@include file="include/includeDatetime.jsp" %>
                <!-- Display Results -->
                <%
                    if (topKNextPlacesResults != null) {

                        // Get the messages from topKNextPlacesResults
                        ArrayList<String> messages = (ArrayList) topKNextPlacesResults.get("messages");

                        ArrayList<LinkedHashMap<String, Object>> topKPopularPlaceList = (ArrayList) topKNextPlacesResults.get("results");

                        // If there are messages, there are errors
                        if (messages != null && messages.size() > 0) {

                            // Print out each error message in the "notification bubble"
                            for (String message : messages) {
                %> 
                <div data-alert class="alert-box alert radius" align="center">
                    <%=message%>
                </div>
                <%
                    }
                } else {
                        // Display "Displaying Results" in the "notification bubble"
                %>
                <div data-alert class="alert-box success radius" align="center">
                    Displaying Results
                </div>
                <%
                        }
                    }
                %>
                <!-- Drop down list for SemanticPlace value -->
                <form method="POST">
                    <!--Semantic Place-->
                    <label><strong>Semantic Place<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <select name="semanticPlace">
                            <%
                                LocationLookupDAO locationLookupDAO = new LocationLookupDAO();
                                ArrayList<LocationLookup> locationList = locationLookupDAO.retrieveAll();
                                for (LocationLookup ll : locationList) {
                                    String sp = ll.getSemanticPlace();
                                    out.print("<option value=" + sp);
                                    if (semanticPlace != null && semanticPlace.equals(sp)) {
                                        out.print(" selected");
                                    }
                                    out.print(">" + sp + "</option>");

                                }
                            %>
                        </select>
                    </label>


                    <!-- Date Time Picker -->
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input name="date" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace('T', ' ')%>" required /> 
                    </label>

                    <!-- K Value-->
                    <label><strong>K value</strong>
                        <select name="k">
                            <option value="1" <% if (k != null && k.equals("1")) {
                                    out.print("selected");
                                } %> >1</option>
                            <option value="2" <% if (k != null && k.equals("2")) {
                                    out.print("selected");
                                } %>>2</option>
                            <option value="3" <% if (k != null && k.equals("3")) {
                                    out.print("selected");
                                } %> >3</option>
                            <option value="4" <% if (k != null && k.equals("4")) {
                                    out.print("selected");
                                } %>>4</option>
                            <option value="5" <% if (k != null && k.equals("5")) {
                                    out.print("selected");
                                } %>>5</option>
                            <option value="6" <% if (k != null && k.equals("6")) {
                                    out.print("selected");
                                } %>>6</option>
                            <option value="7" <% if (k != null && k.equals("7")) {
                                    out.print("selected");
                                } %>>7</option>
                            <option value="8" <% if (k != null && k.equals("8")) {
                                    out.print("selected");
                                } %>>8</option>
                            <option value="9" <% if (k != null && k.equals("9")) {
                                    out.print("selected");
                                } %>>9</option>
                            <option value="10" <% if (k != null && k.equals("10")) {
                                    out.print("selected");
                                }%>>10</option>
                        </select>
                    </label>

                    <!-- Submit Button -->
                    <input type="submit" class="button sloca normal radius " value="Submit">
                </form>
            </div>

            <div class="small-8 columns">
                <%
                    if (topKNextPlacesResults != null) {

                        String status = (String) topKNextPlacesResults.get("status");

                        if (status.equals("success")) {

                            // Retrieve the top k popular places from topKNextPlacesResults
                            ArrayList<LinkedHashMap<String, Object>> topKNextPlacesList = (ArrayList) topKNextPlacesResults.get("results");

                            Integer totalUsers = (Integer) topKNextPlacesResults.get("total-users");
                            Integer nextPlaceUsers = (Integer) topKNextPlacesResults.get("total-next-place-users");


                %>

                <table>
                    <thead>
                    <th>Queried Users</th>
                    <th>Count</th>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Total Users</td>
                            <td><%=totalUsers%></td>
                        </tr>
                        <tr>
                            <td>Total Next Place Users</td>
                            <td><%=nextPlaceUsers%></td>
                        </tr>
                    </tbody>
                </table>
                <%
                    // If there are top k popular places within the timeframe that user specified
                    if (topKNextPlacesList.size() != 0) {
                //Integer totalUsers = (Integer) topKNextPlacesResults.get("total-users");
                        //Integer nextPlaceUsers = (Integer) topKNextPlacesResults.get("total-next-place-users");
                %>


                <table>
                    <thead>
                        <tr>
                            <th style="width: 15%">Rank</th>
                            <th style="width: 50%">Semantic-Place</th>
                            <th style="width: 15%">Count</th>
                            <th style="width: 40%">% of users who left</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (LinkedHashMap<String, Object> result : topKNextPlacesList) {
                        %>
                        <tr>
                            <td style="width: 10%"><center><%=result.get("rank")%></center></td>
                    <td style="width: 50%"><%=result.get("semantic-place")%></td>
                    <%
                        Integer count = (Integer) result.get("count");
                        double percentTravelled = ((double) count / totalUsers) * 100;
                        System.out.println("Original: " + percentTravelled);
                        percentTravelled = Math.round(percentTravelled);
                    %>
                    <td style="width: 15%"><center><%=count%></center></td>
                    <td style="width: 40%"><center><%=(int) percentTravelled + "%"%></center></td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
                <%        }
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
