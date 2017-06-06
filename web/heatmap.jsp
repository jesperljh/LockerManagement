<%-- 
    Document   : heatmap
    Created on : Sep 10, 2014, 3:33:26 AM
    Author     : Jesper
--%>

<%@page import="utility.ErrorMessage"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<%    
    String token = (String) session.getAttribute("token");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JPIntern | Locker</title>

        <!-- Google Fonts -->
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>

        <!-- Zurb Foundations CSS -->
        <link rel="stylesheet" href="css/seat.css" />
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
            <h3 style="display: inline-block;"><strong>&nbspLocker Floorplan</strong></h3>
            <!--Divider-->
            <hr>
        </div>
        <div class="row">
            <div class="small-4 columns">
                <%// @include file="include/includeDatetime.jsp" %>
                <%//                    if (heatmapResults != null) {
                        //If this is not null, then this is a POST and results is stored
                        //Get the list of messagesList, this contains error
                    //    ArrayList<String> messages = (ArrayList) heatmapResults.get("messages");

                    //    ArrayList<HashMap> heatmapList = (ArrayList) heatmapResults.get("heatmap");

                        //If there are errors :(
                    //    if (messages != null && messages.size() > 0) {

                            //For loop to iterate them out
                    //        for (String message : messages) {
                    
                %> 

                <!--<div data-alert class="alert-box alert radius" align="center">
                    <% // =message%>
                </div>-->
                <%
                    /* }
                    //If there is no results found, display error message
                } else if (heatmapList.size() == 0) {
                */
                %>
                <!--<div data-alert class="alert-box alert radius" align="center">
                    <%// =ErrorMessage.getMsg("noResultsFound")%>
                </div>-->
                <%
                    //Display successful results
                // } else {
                %>
                <!-- <div data-alert class="alert-box success radius" align="center">
                    Results Displayed
                </div>-->
                <%
                        // }
                    // }
                %>
                <form action="" method="POST">
                    <label><strong>Floor</strong>
                        <select name="floor" required>
                            <%
                                //Codes here to ensure whatever the user has selected before is selected again - to be more userfriendly
                            %>
                            <option value="5">Level 5</option> 
                            <option value="6">Level 6</option> 
                            <option value="7">Level 7</option>  
                        </select>
                    </label>
                        
                        <label><strong>Locker Cluster</strong>
                        <select name="lockerCluster" required>
                            <%
                                //Codes here to ensure whatever the user has selected before is selected again - to be more userfriendly
                            %>
                            <option value="A-1">A-1</option> 
                            <option value="A-2">A-2</option> 
                            <option value="B-1">B-1</option>
                            <option value="B-2">B-2</option>
                            <option value="B-3">B-3</option>
                            <option value="B-4">B-4</option>
                            <option value="B-5">B-5</option>
                            <option value="C-1">C-1</option>
                            <option value="C-2">C-2</option>
                            <option value="C-3">C-3</option>
                            <option value="C-4">C-4</option>
                            <option value="C-5">C-5</option>
                        </select>
                    </label>
                    <!--Submit-->
                    <input type="submit" value="Submit" class="button sloca normal radius"/>
                    <input type="submit" value="Random Assign" class="button sloca normal radius"/>
                </form>
                        <input type="submit" value="Random Assign For All" class="button sloca normal radius"/>
            </div>

            <%
                //There are results
                /*if (heatmapResults != null) {
                    //Get heatmap data
                    ArrayList<HashMap> heatmapList = (ArrayList) heatmapResults.get("heatmap");

                    //If heatmap data is not empty, and size is bigger than 0 (means this is a successful request!)
                    if (heatmapList != null && heatmapList.size() > 0) {
*/
            %>
            <!-- <div class="small-8 columns">
                <table class="sortable"> 
                    <thead> 
                    <th width="200">Semantic Place</th>
                    <th width="100">Crowd Density</th> 
                    <th width="100">Number of People</th> 
                    </thead>         
                    <tbody> -->
                        <%
                            //retrieves results from the hashmap
                           /* for (HashMap hm : heatmapList) {
                                String semanticPlace = (String) hm.get("semantic-place");
                                int crowdDensity = (Integer) hm.get("crowd-density");
                                int numOfPeople = (Integer) hm.get("num-people");

*/
                        %>
                        <!--Displaying results-->
                        <!--<tr> 
                            <td><center><// =semanticPlace%></center></td> 
                    <td><center><%// =crowdDensity%></center></td> 
                    <td><center><%// =numOfPeople%></center></td> 
                    </tr> -->

                    <%
                       // }
                    %>
                   <!-- </tbody> 
                </table>
            </div>-->
            <%
                //    }
                // }
            %>
        </div>
            <div class="row">
            <!--<div class="medium-8 columns">-->
            <h5> Choose locker by clicking the corresponding locker in the layout below:</h5>
            <div id="holder"> 
                <ul id="place">
                </ul>    
            </div>
            <div style="float:left;"> 
                <ul id="seatDescription">
                    <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_in-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Available Locker</li>
                    <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_out-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Booked Locker</li>
                    <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_ok-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Selected Locker</li>
                </ul>
            </div>
            <div style="clear:both;width:100%">
                <input type="button" id="btnShowNew" value="Show Selected Seats" />
                <input type="button" id="btnShow" value="Show All" />           
            </div>
            <!--</div>-->
        </div>

        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/datetime/foundation.min.js" type="text/javascript"></script>
        <script src="js/jquery-3.2.1.min.js" type="text/javascript"></script>


        <!-- this is the javascript for datetimepicker -->
        <script src="js/datetime/jquery.datetimepicker.js" type="text/javascript"></script>
        <!-- end of the javascript for datetimepicker -->

        <!-- Initialize JS Plugins -->
        <script src="js/datetime/app.js" type="text/javascript"></script>

        <script>
            //$(document).foundation();
            // ********************* YELLOW *******************************
            var settings1 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings2 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            
            // ********************* BLUE *******************************
            var settings3 = {
                rows: 3,
                cols: 10,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings4 = {
                rows: 3,
                cols: 20,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings5 = {
                rows: 3,
                cols: 20,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings6 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings7 = {
                rows: 3,
                cols: 16,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            
            // ********************* YELLOW *******************************
            var settings8 = {
                rows: 3,
                cols: 6,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings9 = {
                rows: 3,
                cols: 6,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 35,
                seatHeight: 35,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings10 = {
                rows: 3,
                cols: 4,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings = {
                rows: 3,
                cols: 20,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings12 = {
                rows: 3,
                cols: 18,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            
            var init = function (reservedSeat) {
                var str = [], seatNo, className;
                for (i = 0; i < settings.rows; i++) {
                    for (j = 0; j < settings.cols; j++) {
                        seatNo = (i + j * settings.rows + 1);
                        className = settings.seatCss + ' ' + settings.rowCssPrefix + i.toString() + ' ' + settings.colCssPrefix + j.toString();
                        if ($.isArray(reservedSeat) && $.inArray(seatNo, reservedSeat) != -1) {
                            className += ' ' + settings.selectedSeatCss;
                        }
                        str.push('<li class="' + className + '"' +
                                'style="top:' + (i * settings.seatHeight).toString() + 'px;left:' + (j * settings.seatWidth).toString() + 'px;">' +
                                '<a style="color: white; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black; font-size: 15px" title="' + seatNo + '">' + seatNo + '</a>' +
                                '</li>');
                    }
                }
                $('#place').html(str.join(''));
            };
            //case I: Show from starting
            //init();

            //Case II: If already booked
            var bookedSeats = [5, 10, 25];
            init(bookedSeats);


            $('.' + settings.seatCss).click(function () {
                if ($(this).hasClass(settings.selectedSeatCss)) {
                    alert('This seat is already reserved');
                } else {
                    $(this).toggleClass(settings.selectingSeatCss);
                }
            });

            $('#btnShow').click(function () {
                var str = [];
                $.each($('#place li.' + settings.selectedSeatCss + ' a, #place li.' + settings.selectingSeatCss + ' a'), function (index, value) {
                    str.push($(this).attr('title'));
                });
                alert(str.join(','));
            })

            $('#btnShowNew').click(function () {
                var str = [], item;
                $.each($('#place li.' + settings.selectingSeatCss + ' a'), function (index, value) {
                    item = $(this).attr('title');
                    str.push(item);
                });
                alert(str.join(','));
            })
        </script>
    </body>
</html>
