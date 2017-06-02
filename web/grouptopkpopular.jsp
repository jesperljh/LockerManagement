<%-- 
    Document   : grouptopkpopular
    Created on : Sep 10, 2014, 3:33:26 AM
    Author     : Eugene/Kenneth
--%>

<%@page import="controller.GroupTopKPopularController"%>
<%@page import="utility.ErrorMessage"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="controller.HeatmapController"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    //Declare HashMap to store heatmap results 
    Demographics currentUser = (Demographics) session.getAttribute("user");
    HashMap<String, Object> groupResults = null;

    //Declare variables
    String dateTime = "";
    String k = "";
    String token = (String) session.getAttribute("token");
    //if this is a post
    if (request.getMethod() != null && request.getMethod().equals("POST")) {

        //This replaces the ' ' with a T as required by the controller.
        dateTime = request.getParameter("date").replace(" ", "T");

        k = request.getParameter("k");

        //This stores the datetime for transferring between from page to page.
        session.setAttribute("datetimeInput", dateTime);

        //Get top k results
        GroupTopKPopularController gprc = new GroupTopKPopularController();
        groupResults = gprc.getTopKPopularPlaces(dateTime, k, token, false);

    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Group Top K Popular Places</title>

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
            <i class="fi-map size-48"></i>
            <h3 style="display: inline-block;"><strong>&nbspGroup Top-K Popular Places</strong></h3>
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

                        ArrayList<HashMap> groupList = (ArrayList) groupResults.get("results");

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
                <%
                        }
                    }
                %>
                <form action="" method="POST">
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input name="date" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace("T", " ")%>" required/> 
                    </label>

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

                    <!--Submit-->
                    <input type="submit" value="Submit" class="button sloca normal radius"/>
                </form>
            </div>
            <%
                //There are results
                if (groupResults != null) {
                    //Get heatmap data

                    ArrayList<HashMap> groupList = (ArrayList) groupResults.get("results");

                    //If heatmap data is not empty, and size is bigger than 0 (means this is a successful request!)
                    if (groupList != null && groupList.size() > 0) {
            %>
            <div class="small-8 columns">
                <table class="sortable"> 
                    <thead> 
                    <th style="width:10%">Rank</th>
                    <th style="width:50%">Semantic Place</th> 
                    <th style="width:40%">Number of Groups</th> 
                    </thead>         
                    <tbody>
                        <%
                            for (HashMap group : groupList) {
                                int rank = (Integer) group.get("rank");
                                String semanticPlace = (String) group.get("semantic-place");
                                int numberOfGroups = (Integer) group.get("num-groups");
                        %>
                        <tr> 
                            <td><center><%=rank%></center></td> 
                    <td><center><%=semanticPlace%></center></td> 
                    <td><center><%=numberOfGroups%></center></td> 
                    </tr> 

                    <%
                        }
                    %>
                    </tbody> 
                </table>
            </div>
            <%
                    }
                }
            %>
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
