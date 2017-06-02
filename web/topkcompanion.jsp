<%-- 
    Document   : topkcompanion
    Created on : Sep 28, 2014, 8:38:32 PM
    Author     : user
--%>


<%@page import="utility.ErrorMessage"%>
<%@page import="controller.TopKCompanionController"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dao.DemographicsDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp"%>
<%    LinkedHashMap<String, Object> topKCompanionResults = null;
    String macAddress = "";
    String dateTime = "";
    String kValue = "";
    //retrieve of token for validation
    String token = (String) session.getAttribute("token");

    if (request.getMethod() != null && request.getMethod().equals("POST")) {
        macAddress = request.getParameter("mac-address");
        dateTime = request.getParameter("date").replace(" ", "T");
        kValue = request.getParameter("k");
        TopKCompanionController topKCompanionController = new TopKCompanionController();
        topKCompanionResults = topKCompanionController.getTopKCompanion(macAddress, dateTime, kValue, token, false);
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Basic Location Report | Top-K Companion </title>
        <link rel="stylesheet" href="css/foundation.css" />
        <link rel="stylesheet" href="css/jquery-ui.css" />
        <link rel="stylesheet" href="css/jquery-ui.structure.css" />
        <link rel="stylesheet" href="css/jquery-ui.theme.css" />
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
            <i class="fi-torsos-male-female size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspTop-K Companions</strong></h3>
            <!--Divider-->
            <hr>
        </div>
        <div class="row">
            <div class="small-4 columns">
                <%@include file="include/includeDatetime.jsp" %>        
                <%
                    if (topKCompanionResults != null) {
                        ArrayList<String> messagesList = (ArrayList) topKCompanionResults.get("messages");

                        ArrayList<LinkedHashMap> resultList = (ArrayList) topKCompanionResults.get("results");

                        if (messagesList != null && messagesList.size() > 0) {
                            for (String message : messagesList) {
                %> 
                <div data-alert class="alert-box alert radius" align="center">
                    <%=message%>
                    <a href="#" class="close">&times;</a>
                </div>
                <%
                    }
                } else if (resultList.size() == 0) {
                %>
                <div data-alert class="alert-box alert radius" align="center">
                    <%=ErrorMessage.getMsg("noResultsFound")%>
                    <a href="#" class="close">&times;</a>
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
                    <label><strong>Mac-Address<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input type="text" name="mac-address" id="mac-address" value="<%=macAddress%>" required />
                    </label>
                    <label><strong>Date & Time<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                        <input name="date" type="text" placeholder="Select a Date & Time" id="default_datetimepicker" value="<%=dateTime.replace("T", " ")%>" required/> 
                    </label>

                    <label><strong>K Value</strong>
                        <select name="k">
                            <option value="1" <% if (kValue != null && kValue.equals("1")) {
                                    out.print("selected");
                                } %> >1</option>
                            <option value="2" <% if (kValue != null && kValue.equals("2")) {
                                    out.print("selected");
                                } %>>2</option>
                            <option value="3" <% if (kValue != null && kValue.equals("3")) {
                                    out.print("selected");
                                } %>>3</option>
                            <option value="4" <% if (kValue != null && kValue.equals("4")) {
                                    out.print("selected");
                                } %>>4</option>
                            <option value="5" <% if (kValue != null && kValue.equals("5")) {
                                    out.print("selected");
                                } %>>5</option>
                            <option value="6" <% if (kValue != null && kValue.equals("6")) {
                                    out.print("selected");
                                } %>>6</option>
                            <option value="7" <% if (kValue != null && kValue.equals("7")) {
                                    out.print("selected");
                                } %>>7</option>
                            <option value="8" <% if (kValue != null && kValue.equals("8")) {
                                    out.print("selected");
                                } %>>8</option>
                            <option value="9" <% if (kValue != null && kValue.equals("9")) {
                                    out.print("selected");
                                } %>>9</option>
                            <option value="10" <% if (kValue != null && kValue.equals("10")) {
                                    out.print("selected");
                                }%>>10</option>
                        </select>
                    </label>

                    <!-- Submit Button -->
                    <input type="submit" value="Submit" class="button sloca normal radius"/>
                </form>
            </div>
            <%
                if (topKCompanionResults != null) {
                    String status = (String) topKCompanionResults.get("status");
                    if (status.equals("success")) {
                        ArrayList<LinkedHashMap<String, Object>> resultList = (ArrayList<LinkedHashMap<String, Object>>) topKCompanionResults.get("results");
                        if (resultList.size() != 0) {
            %>
            <div class="small-8 column">
                <table>
                    <thead>
                        <tr>
                            <th style="width: 8%">Rank</th>
                            <th style="width: 27%">Companion</th>
                            <th style="width: 60%">Mac-Address</th>
                            <th style="width: 15%">Time Together</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (LinkedHashMap<String, Object> result : resultList) {
                        %>
                        <tr>
                            <td style="word-wrap: break-word"><%=result.get("rank")%></td>
                            <td style="word-wrap: break-word"><%=result.get("companion")%></td>
                            <td style="word-wrap: break-word"><%=result.get("mac-address")%></td>
                            <td style="word-wrap: break-word"><%=result.get("time-together")%></td>
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
        <script src="js/vendor/jquery-ui.js"></script>
        <script>
            $(document).ready(function() {
            <%
                DemographicsDAO demographicsDAO = new DemographicsDAO();
                ArrayList<Demographics> demographicsList = demographicsDAO.retrieveAll();
                String availableTags = "";
                for (Demographics d : demographicsList) {
                    availableTags += "\t\t\t\"" + d.getMacAddress() + "\",\r\n";

                }
            %>
                var availableTags = [
            <%=availableTags%>
                ];
                $("#mac-address").autocomplete({
                    source: availableTags,
                    minLength: 2,
                });
            });
            $(document).foundation();
        </script>
    </body>
</html>