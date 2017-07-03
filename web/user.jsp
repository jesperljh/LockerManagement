<%-- 
    Document   : heatmap
    Created on : Sep 10, 2014, 3:33:26 AM
    Author     : Jesper
--%>

<%@page import="java.util.Map"%>
<%@page import="entity.Request"%>
s<%@page import="controller.RequestController"%>
<%@page import="entity.Locker"%>
<%@page import="controller.LockerController"%>
<%@page import="dao.LockerDAO"%>
<%@page import="utility.ErrorMessage"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<%    String token = (String) session.getAttribute("token");
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
        <input type="hidden" name="currentNB" id="currentNB" value="<%=currentUser.getNeighbourhood()%>">
        <div id="clusterInfo">
            <%
                String cluster = request.getParameter("lockerCluster");
                LockerController locker_Ctrl = new LockerController();
                HashMap<String, ArrayList<Locker>> mapLockerList = locker_Ctrl.getLockerClusterListByNeighbourhood(currentUser.getNeighbourhood());

                if (cluster == null) {
                    cluster = mapLockerList.keySet().iterator().next();
                }
                //int clusterNo = 0;
                ArrayList<Locker> value = new ArrayList<Locker>();
                out.println("<input type='checkbox' name='cluster' id='cluster' value='" + cluster + "' hidden>");
                value = mapLockerList.get(cluster);
                if (value != null) {
                    for (Locker locker : value) {
                        out.println("<input type='checkbox' name='lockerNo' id='lockerNo' value='" + locker.getLocker_no() + "' hidden>");
                        if (locker.getTaken_by() != null) {
                            out.println("<input type='checkbox' name='taken_by' id='taken_by' value='" + locker.getLocker_no() + "' hidden>");
                        }
                    }
                }
            %>
        </div>
        <!--Page Header-->
        <div class="row" style="padding-top: 30px; padding-left: 18px; padding-right: 18px">
            <i class="fi-map size-48"></i>
            <h3 style="display: inline-block;"><strong>&nbspLocker Floorplan</strong></h3>
            <!--Divider-->
            <hr>
        </div>
        <div class="row">
            <div class="small-4 columns">
                <%                    LockerController lockerCtrl = new LockerController();
                    Locker myLocker = lockerCtrl.getLockerBySid(currentUser.getSid());
                    if (myLocker == null) {
                        out.println("<input type='checkbox' name='myLocker' id='myLocker' value='A0' hidden>");
                %>
                <h4 style="color: teal"><strong>My Locker : </strong>No Locker</h4> 
                <%
                } else {
                    out.println("<input type='checkbox' name='myLocker' id='myLocker' value='" + myLocker.getLocker_no() + "' hidden>");
                %>
                <h4 style="color: teal"><strong>My Locker : <%= myLocker.getLocker_no()%></strong></h4>
                <%
                    }
                %>
                <hr>
            </div>
        </div>

        <%
            RequestController requestController = new RequestController();
            ArrayList<Request> requestList = requestController.getRequestsBySid(currentUser.getSid());
            if (requestList != null && requestList.size() != 0) {
        %>

        <div class="row">
            <div class="people-you-might-know">
                <div class="add-people-header">
                    <h6 class="header-title">
                        My Request
                    </h6>
                </div>
                <%
                    for (Request r : requestList) {
                %>
                <div class="row add-people-section" style="margin-left: 0px; margin-right: 0px">
                    <div class="small-12 medium-6 columns about-people">
                        <div class="about-people-author" style="margin-left: 20px">
                            <p class="author-name">
                                Requester : <%=r.getRequester()%>
                            </p>
                            <p class="author-location">
                                <i class="fa fa-map-marker" aria-hidden="true"></i>
                                Locker number : <%=r.getLockerNo()%>
                            </p>
                            <p class="author-mutual">
                                Status : <strong><%=r.getStatus()%></strong>
                            </p>
                        </div>    
                    </div>
                    <div class="small-12 medium-6 columns add-friend">
                        <div class="add-friend-action">
                            <a href="/LockerAssignment/processRequest.jsp?request=accept&id=<%=r.getId()%>&mySid=<%=r.getReceiver()%>&rSid=<%=r.getRequester()%>" class="radius button small">Accept Request</a>
                            <a href="/LockerAssignment/processRequest.jsp?request=reject&id=<%=r.getId()%>&mySid=<%=r.getReceiver()%>&rSid=<%=r.getRequester()%>" class="button radius secondary small">Reject Request</a>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        </div>  
        <%
            }
        %>
        <div class="row">
            <form action="user.jsp" method="POST">
                <hr>
                <div class="small-4 columns">

                    <input type="hidden" name="nb" value="<%=currentUser.getNeighbourhood()%>">
                    <label><strong>Locker Cluster</strong>
                        <select name="lockerCluster" required>
                            <%
                                LockerController locker_ctrl = new LockerController();
                                if (currentUser.getNeighbourhood() != null) {
                                    HashMap<String, ArrayList<Locker>> lockerMap = locker_ctrl.getLockerClusterListByNeighbourhood(currentUser.getNeighbourhood());
                                    for (Map.Entry<String, ArrayList<Locker>> entry : lockerMap.entrySet()) {
                                        String key = (String) entry.getKey();
                            %>
                            <option value="<%=key%>"><%=key%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </label>
                </div>
                <div class="small-8 columns">
                    <!--Submit-->
                    <input type="submit" value="Select Cluster" style="margin-top: 10px" class="button sloca normal radius"/>
                </div>
            </form>
        </div>
        <form action="lockerRequestServlet" method="POST">
            <input type="hidden" name="selectedLocker" id="selectedLocker" value="">
            <input type="hidden" name="requester" id="requester" value="<%=currentUser.getSid()%>">
            <input type="hidden" name="myCluster" id="myCluster" value="">
            <div class="row" name="displayLocker" id="displayLocker">
                <!--<div class="medium-8 columns">-->
                <!-- <h5> Choose locker by clicking the corresponding locker in the layout below:</h5>
                 <div id="holder"> 
                     <ul id="place">
                     </ul>    
                 </div>
                 <div style="float:left;"> 
                     <ul id="seatDescription">
                         <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_in-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Available Locker</li>
                         <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_out-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Booked Locker</li>
                         <li style="background:url('https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_ok-24.png') no-repeat scroll 0 0 transparent; padding-right: 30px">Selected Locker</li>
                         <li style="background:url('https://png.icons8.com/safe/android/24') no-repeat scroll 0 0 transparent; padding-right: 30px">Restricted Locker</li>
                     </ul>
                 </div>
                 <div style="clear:both;width:100%">
                     <input type="button" id="btnShowNew" value="Show Selected Seats" />
                     <input type="button" id="btnShow" value="Show All" />           
                 </div>-->
                <!--</div>-->
            </div>
            <div class="row">
                <input type="submit" value="Request To Swap Locker" style="margin-top: 10px" class="button sloca normal radius"/>
            </div>
        </form>
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

            // ********************* YELLOW *******************************
            var settings = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat',
                restrictedSeatCss: 'restrictedSeat'
            };
            var settings1 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat',
                restrictedSeatCss: 'restrictedSeat'
            };
            var settings2 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
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
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings4 = {
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
            var settings5 = {
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
            var settings6 = {
                rows: 3,
                cols: 8,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings7 = {
                rows: 3,
                cols: 16,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
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
                seatWidth: 50,
                seatHeight: 50,
                seatCss: 'seat',
                selectedSeatCss: 'selectedSeat',
                selectingSeatCss: 'selectingSeat'
            };
            var settings9 = {
                rows: 3,
                cols: 6,
                rowCssPrefix: 'row-',
                colCssPrefix: 'col-',
                seatWidth: 50,
                seatHeight: 50,
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
            var settings11 = {
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

            //Case II: If already booked
            //var bookedSeats = [5, 10, 25];
            var bookedSeats = [];
            var restrictSeats = [];
            var myLocker = [];
            //var countCluster = document.getElementById('noOfCluster').value;
            var initialiseLocker = function (key) {
                key = document.getElementById("cluster").value;
                bookedSeats = [];
                restrictSeats = [];
                $("input[name='lockerNo']").each(function () {
                    value = $(this).val();
                    if (value.substring(0, 1) == 'A') {
                        if (parseInt(value.substring(1)) >= 25) {
                            restrictSeats.push(parseInt(value.substring(1)) - 24);
                        } else {
                            restrictSeats.push(parseInt(value.substring(1)));
                        }
                    } else {
                        restrictSeats.push(parseInt(value.substring(1)));
                    }
                });
                $("input[name='taken_by']").each(function () {
                    value = $(this).val();
                    bookedSeats.push(parseInt(value.substring(1)));
                });
                $("input[name='myLocker']").each(function () {
                    value = $(this).val();
                    myLocker.push(parseInt(value.substring(1)));
                });
                if (key == "rat") {
                    settings = settings1;
                } else if (key == "ox") {
                    settings = settings2;
                } else if (key == "tiger") {
                    settings = settings3;
                } else if (key == "rabbit") {
                    settings = settings4;
                } else if (key == "dragon") {
                    settings = settings5;
                } else if (key == "snake") {
                    settings = settings6;
                } else if (key == "horse") {
                    settings = settings7;
                } else if (key == "sheep") {
                    settings = settings8;
                } else if (key == "monkey") {
                    settings = settings9;
                } else if (key == "rooster") {
                    settings = settings10;
                } else if (key == "dog") {
                    settings = settings11;
                } else if (key == "pig") {
                    settings = settings12;
                }

                document.getElementById("myCluster").value = key;

                var displayLocker = "<row><div class='medium-8 columns'>" +
                        "<h5> Cluster " + key + ": </h5>" +
                        "<div id='holder'> <ul id='place'></ul>    </div>" +
                        "<div style='float:left; width: 750px'>" +
                        "<ul id='seatDescription'>" +
                        "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_in-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Available Locker</li>" +
                        "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_out-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Booked Locker</li>" +
                        "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_ok-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Selected Locker</li>" +
                        "<li style='background:url(\"https://png.icons8.com/safe/android/24\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Restricted Locker</li>" +
                        "</ul>" +
                        "</div></row>";


                $('#displayLocker').html(displayLocker);


                return restrictSeats;
            };
            //$(document).foundation();

            //case I: Show from starting
            //init();
            var init = function (reservedSeat) {
                if (document.getElementById('cluster') != null) {
                    reservedSeat = initialiseLocker(document.getElementById("cluster").value);
                    var str = [], seatNo, className;
                    for (i = 0; i < settings.rows; i++) {
                        for (j = 0; j < settings.cols; j++) {
                            seatNo = (i + j * settings.rows + 1);
                            className = settings.seatCss + ' ' + settings.rowCssPrefix + i.toString() + ' ' + settings.colCssPrefix + j.toString();
                            if ($.isArray(reservedSeat) && $.inArray(seatNo, reservedSeat) != -1) {
                                if ($.isArray(bookedSeats) && $.inArray(seatNo, bookedSeats) != -1) {
                                    className += ' ' + settings.selectedSeatCss;
                                }
                            } else {
                                //className += ' ' + settings.selectedSeatCss;
                                className += ' ' + settings.restrictedSeatCss;
                            }

                            if ($.isArray(myLocker) && $.inArray(seatNo, myLocker) != -1) {
                                className += ' myLocker';
                            }

                            str.push('<li class="' + className + '"' +
                                    'value="' + seatNo + '" style="top:' + (i * settings.seatHeight).toString() + 'px;left:' + (j * settings.seatWidth).toString() + 'px;">' +
                                    '<a value="' + seatNo + '" style="color: white; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black; font-size: 15px" title="' + seatNo + '">' + seatNo + '</a>' +
                                    '</li>');
                        }
                    }
                    $('#place').html(str.join(''));
                }

            };
            init(bookedSeats);
            $('.' + settings.seatCss).click(function () {
                if ($(this).hasClass(settings.restrictedSeatCss)) {
                    alert('This locker is not under your neighbourhood');
                } else if ($(this).hasClass('myLocker')) {
                    alert('You selected your own locker. Please select other locker.');
                } else if ($(this).hasClass(settings.selectedSeatCss)) {
                    //alert('This seat is already reserved');
                    $("ul li").removeClass(setting.selectingSeatCss);
                    $(this).addClass(setting.selectingSeatCss);
                    document.getElementById("selectedLocker").value = $(this).text();
                } else if ($(this).hasClass(settings.selectingSeatCss)) {
                    $("ul li").removeClass(setting.selectingSeatCss);
                    document.getElementById("selectedLocker").value = "";
                } else {
                    $("ul li").removeClass(setting.selectingSeatCss);
                    $(this).addClass(setting.selectingSeatCss);
                    document.getElementById("selectedLocker").value = $(this).text();
                    //$(this).toggleClass(settings.selectingSeatCss);
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
