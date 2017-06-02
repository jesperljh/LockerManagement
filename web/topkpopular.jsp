<%-- 
    Document   : topkpopular
    Created on : Sep 28, 2014, 3:37:56 PM
    Author     : Jiacheng
--%>

<%@page import="utility.ErrorMessage"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="controller.TopKPopularPlaceController"%>
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

    //retrieve of token for validation
    String token = (String) session.getAttribute("token");

    // Initialize topKPopularPlaceResults as null
    LinkedHashMap<String, Object> topKPopularPlaceResults = null;

    if (request.getMethod() != null && request.getMethod().equals("POST")) {

        // Instantiating locationLookupDAO
        LocationLookupDAO locationLookupDAO = new LocationLookupDAO();

        // Instantiating TopKPopularPlaceController
        TopKPopularPlaceController topKPopularPlaceController = new TopKPopularPlaceController();

        // This replaces the ' ' with a T as required by the controller
        dateTime = request.getParameter("date").replace(' ', 'T');

        // Retrieve the k value that user has selected
        k = request.getParameter("k");

        // Retrieve and Instantiate the results from TopKPopularPlaceController
        topKPopularPlaceResults = topKPopularPlaceController.getTopKPopularPlace(k, dateTime, token, false);
    }
%>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Basic Location Report | Top-K Popular Places</title>
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
            <i class="fi-flag size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspTop-K Popular Places</strong></h3>
            <hr/>
        </div>

        <div class="row">
            <div class="small-4 columns">
                <%@include file="include/includeDatetime.jsp" %>
                <!-- Display Results -->
                <%
                    if (topKPopularPlaceResults != null) {

                        // Get the messages from topKPopularPlaceResults
                        ArrayList<String> messages = (ArrayList) topKPopularPlaceResults.get("messages");

                        ArrayList<LinkedHashMap<String, Object>> topKPopularPlaceList = (ArrayList) topKPopularPlaceResults.get("results");

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
                    Results Displayed
                </div>

                <%
                        }
                    }
                %>

                <!-- Drop down list for K value -->

                <form method="POST">
                    <!-- Date Time Picker -->
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input name="date" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace('T', ' ')%>" required/> 
                    </label>

                    <!-- K Value -->
                    <label><strong>K</strong>
                        <select name="k">
                            <option value="1" <% if (k != null && k.equals("1")) {
                                    out.print("selected");
                                } %> >1</option>
                            <option value="2" <% if (k != null && k.equals("2")) {
                                    out.print("selected");
                                } %>>2</option>
                            <option value="3" <% if (k != null && k.equals("3")) {
                                    out.print("selected");
                                } %>>3</option>
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
                    <input type="submit" class="button sloca normal radius" value="Submit">
                </form>
            </div>
            <%
                if (topKPopularPlaceResults != null) {

                    // Retrieve the top k popular places from topKPopularPlaceResults
                    ArrayList<LinkedHashMap<String, Object>> topKPopularPlaceList = (ArrayList) topKPopularPlaceResults.get("results");

                    // If there are top k popular places within the timeframe that user specified
                    if (topKPopularPlaceList != null && topKPopularPlaceList.size() > 0) {
            %>
            <div class="small-8 columns">
                <table> 
                    <thead> 
                        <tr> 
                            <th style="width:20%">Rank</th>
                            <th style="width:45%">Semantic Place</th>
                            <th style="width:35%">Number of People</th>
                        </tr> 
                    </thead>         
                    <tbody>
                        <%
// Print each popular place retrieved
                            for (LinkedHashMap<String, Object> popularPlace : topKPopularPlaceList) {
                                int rank = (Integer) popularPlace.get("rank");
                                String semanticPlace = (String) popularPlace.get("semantic-place");
                                int count = (Integer) popularPlace.get("count");
                        %>
                        <tr>
                            <td style="width:20%"><center><%=rank%></center></td> 
                    <td style="width:60%"><center><%=semanticPlace%></center></td> 
                    <td style="width:20%"><center><%=count%></center></td> 
                    </tr>
                    <%
                        }

                    %>
                    </tbody> 
                </table>
            </div>
            <%        } else {
            %>
            <div class="small-8 columns">
                <table> 
                    <thead> 
                        <tr> 
                            <th style="width:20%">Rank</th>
                            <th style="width:45%">Semantic Place</th>
                            <th style="width:35%">Number of People</th>
                        </tr> 
                    </thead>         
                    <tbody>
                        <tr>
                            <td>N/A</td>
                            <td>N/A</td>
                            <td>0</td>
                        </tr>
                    </tbody> 
                </table>
            </div>
            <%
                    }
                }
            %>
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
