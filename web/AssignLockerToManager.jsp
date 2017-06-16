<%-- 
    Document   : AssignLockerToManager
    Created on : Jun 6, 2017, 2:41:28 PM
    Author     : Jesper
--%>
<%@page import="entity.Locker"%>
<%@page import="java.util.HashMap"%>
<%@page import="controller.LockerController"%>
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
        if (sid == null) {
            response.sendRedirect("bootstrap.jsp");
        }
        DemographicsCSVController demographicController = new DemographicsCSVController();
        Demographics manager = demographicController.getUser(sid);
    %>
    <body>
        <%@include file="include/admintopbar.jsp" %>
        <!--Page Header-->
        <div class="row" style="padding-top: 50px">
            <!--Icon-->
            <i class="fi-upload size-48"></i>
            <!--Title-->
            <h3 class="page-header" style="display: inline-block"><strong>&nbspAssign Locker Cluster to <%=manager.getNeighbourhood().toUpperCase()%> Manager</strong></h3>
            <!--Divider-->
            <hr>

            <form action="lockerClusterServlet" method="POST">
                <input type="hidden" id="neighbourhood" name="neighbourhood" value="<%=manager.getNeighbourhood()%>">

                <%
                    //  i want to get every cluster, for each cluster what is the available 
                    LockerController lc = new LockerController();
                    HashMap<String, ArrayList<Locker>> clusterMap = lc.getLockerClusterList();

                    int ratCount = lc.countFreeLockers(clusterMap.get("rat"));
                    int oxCount = lc.countFreeLockers(clusterMap.get("ox"));
                    int tigerCount = lc.countFreeLockers(clusterMap.get("tiger"));
                    int rabbitCount = lc.countFreeLockers(clusterMap.get("rabbit"));
                    int dragonCount = lc.countFreeLockers(clusterMap.get("dragon"));
                    int snakeCount = lc.countFreeLockers(clusterMap.get("snake"));
                    int horseCount = lc.countFreeLockers(clusterMap.get("horse"));
                    int sheepCount = lc.countFreeLockers(clusterMap.get("sheep"));
                    int monkeyCount = lc.countFreeLockers(clusterMap.get("monkey"));
                    int roosterCount = lc.countFreeLockers(clusterMap.get("rooster"));
                    int dogCount = lc.countFreeLockers(clusterMap.get("dog"));
                    int pigCount = lc.countFreeLockers(clusterMap.get("pig"));

                %>

                <%                            if (ratCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_rat" name="check_rat" value="rat"> Rat Locker Cluster<br>                     
                    <select id="rat" name="rat" hidden>
                        <%
                            for (int i = 1; i <= ratCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>
                <% } %>
                
                <%                            if (oxCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_ox" name="ox" value="ox"> Ox Locker Cluster<br>
                    <select id="ox" hidden>
                        <%
                            for (int i = 1; i <= oxCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>
                    <% } %>
                    
                    <%                            if (tigerCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_tiger" name="tiger" value="tiger"> Tiger Locker Cluster<br>
                    <select id="tiger" hidden>
                        <%
                            for (int i = 1; i <= tigerCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row> 
                    <% } %>
                    
                    <%                            if (rabbitCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_rabbit" name="rabbit" value="rabbit"> Rabbit Locker Cluster<br>  
                    <select id="rabbit" hidden>
                        <%
                            for (int i = 1; i <= rabbitCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (dragonCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_dragon" name="dragon" value="dragon"> Dragon Locker Cluster<br>
                    <select id="dragon" hidden>
                        <%
                            for (int i = 1; i <= dragonCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (snakeCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_snake" name="snake" value="snake"> Snake Locker Cluster<br>
                    <select id="snake" hidden>
                        <%
                            for (int i = 1; i <= snakeCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (horseCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_horse" name="horse" value="horse"> Horse Locker Cluster<br> 
                    <select id="horse" hidden>
                        <%
                            for (int i = 1; i <= horseCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (sheepCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_sheep" name="sheep" value="sheep"> Sheep Locker Cluster<br>
                    <select id="sheep" hidden>
                        <%
                            for (int i = 1; i <= sheepCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (monkeyCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_monkey" name="monkey" value="monkey"> Monkey Locker Cluster<br>  
                    <select id="monkey" hidden>
                        <%
                            for (int i = 1; i <= monkeyCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (roosterCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_rooster" name="rooster" value="rooster"> Rooster Locker Cluster<br>
                    <select id="rooster" hidden>
                        <%
                            for (int i = 1; i <= roosterCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (dogCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_dog" name="dog" value="dog"> Dog Locker Cluster<br> 
                    <select id="dog" hidden>
                        <%
                            for (int i = 1; i <= dogCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                    
                    <%                            if (pigCount != 0) {
                %>
                <row>
                    <input type="checkbox" id="check_pig" name="pig" value="pig"> Pig Locker Cluster<br> 
                    <select id="pig" hidden>
                        <%
                            for (int i = 1; i <= pigCount; i++) {
                        %>  <option value=<%=i%>><%=i%></option>    <%
                            }
                        %>
                    </select>
                </row>  
                    <% } %>
                <row>
                    <input style="margin-top: 15px" type="submit" value="Assign" class="button sloca normal radius"/>
                </row> 

            </form>
        </div>

        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/foundation.min.js"></script>
        <script>
            $(document).foundation();

            $('#check_rat').click(function () {
                $('#rat')[this.checked ? "show" : "hide"]();
            });

            $('#check_ox').click(function () {
                $('#ox')[this.checked ? "show" : "hide"]();
            });

            $('#check_tiger').click(function () {
                $('#tiger')[this.checked ? "show" : "hide"]();
            });

            $('#check_rabbit').click(function () {
                $('#rabbit')[this.checked ? "show" : "hide"]();
            });

            $('#check_dragon').click(function () {
                $('#dragon')[this.checked ? "show" : "hide"]();
            });

            $('#check_snake').click(function () {
                $('#snake')[this.checked ? "show" : "hide"]();
            });

            $('#check_horse').click(function () {
                $('#horse')[this.checked ? "show" : "hide"]();
            });

            $('#check_sheep').click(function () {
                $('#sheep')[this.checked ? "show" : "hide"]();
            });

            $('#check_monkey').click(function () {
                $('#monkey')[this.checked ? "show" : "hide"]();
            });

            $('#check_rooster').click(function () {
                $('#rooster')[this.checked ? "show" : "hide"]();
            });

            $('#check_dog').click(function () {
                $('#dog')[this.checked ? "show" : "hide"]();
            });

            $('#check_pig').click(function () {
                $('#pig')[this.checked ? "show" : "hide"]();
            });





        </script>
    </body>
</html>