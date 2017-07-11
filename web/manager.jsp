<%-- 
    Document   : manager
    Created on : Jun 12, 2017, 9:50:36 AM
    Author     : Jesper
--%>

<%@page import="controller.DemographicsCSVController"%>
<%@page import="java.util.Map"%>
<%@page import="entity.Locker"%>
<%@page import="controller.LockerController"%>
<%@page import="utility.ErrorMessage"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="include/protect.jsp" %>
<%    String token = (String) session.getAttribute("token");
    /*String error = (String) request.getAttribute("error");
    if (error != null) {
        out.println("<script>alert('" + error + "');</script>");
    }*/
%>
<!DOCTYPE html>
<html>
    <style>
        .unUsedBold {
            background-color: #DEB887;
            font-weight: bold;
        }
        .usedBold{
            background-color: #DEB887;
            font-weight: bold;
        }
    </style>


    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JPIntern | Locker</title>

        <!-- Google Fonts -->
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>

        <!-- Zurb Foundations CSS -->
        <link rel="stylesheet" href="css/seat.css" />
        <link rel="stylesheet" href="css/app.css" />
        <link rel="stylesheet" href="css/foundation.css" />
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">
        <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css">


        <!-- DatePicker CSS & Javascript-->
        <link rel="stylesheet" href="css/datetime/jquery.datetimepicker.css">
        <script src="js/vendor/modernizr.js"></script>

        <!-- SortTable -->
        <script src="js/sorttable.js"></script>
    </head>
    <body>
        <%@include file="include/topbar.jsp" %>
        <input type="hidden" name="currentNB" id="currentNB" value="<%=currentUser.getNeighbourhood()%>">
        <%
            LockerController lockerCtrl = new LockerController();
            HashMap<String, ArrayList<Locker>> mapLockerList = lockerCtrl.getLockerClusterListByNeighbourhood(currentUser.getNeighbourhood());
            int clusterNo = 0;
            for (Map.Entry<String, ArrayList<Locker>> entry : mapLockerList.entrySet()) {
                String key = entry.getKey();
                ArrayList<Locker> value = entry.getValue();
                if (key != null) {
                    clusterNo++;
                    out.println("<input type='checkbox' name='cluster" + clusterNo + "' id='cluster" + clusterNo + "' value='" + key + "' hidden>");
                }
                if (value != null) {
                    for (Locker locker : value) {
                        out.println("<input type='checkbox' name='lockerNo" + clusterNo + "' id='lockerNo" + clusterNo + "' value='" + locker.getLocker_no() + "' hidden>");
                    }
                }
            }
            out.println("<input type='checkbox' name='noOfCluster' id='noOfCluster' value='" + clusterNo + "' hidden>");
        %>
        <!--Page Header-->
        <div class="row" style="padding-top: 30px; padding-left: 18px; padding-right: 18px">
            <i class="fi-map size-48"></i>
            <h3 style="display: inline-block;"><strong>&nbspAssign Locker To User</strong></h3>
            <!--Divider-->
            <hr>
        </div>

        <div class="row">
            <%
                String errorMsg = (String) request.getAttribute("error");
                String success = (String) request.getAttribute("success");
                if (errorMsg != null) {
            %>
            <div data-alert class="alert-box round" style="background-color: #5e001f">
                <%=errorMsg%>
                <a href="#" class="close" style="color: whitesmoke; font-size: 25px">&times;</a>
            </div>
            <%
            } else if (success != null) {
            %>
            <div data-alert class="alert-box success round">
                <%=success%>
                <a href="#" class="close" style="color: whitesmoke; font-size: 25px">&times;</a>
            </div>
            <%
                }
                request.setAttribute("error", null);
                request.setAttribute("success", null);
            %>
            <form action="assignLockerServlet" method="POST">
                <div>
                    <div class="row">
                        <div class="small-5 columns deepshd"><strong>User Without Locker</strong></div>
                        <div class="small-2 columns"></div>
                        <div class="small-5 columns deepshd" style="margin-bottom: 20px"><strong>Selected User To Assign Locker</strong></div>
                    </div>
                    <div class="row">

                        <!-- Retrieved users list from the neighbourhood -->

                        <div style="overflow: scroll; height: 300px; border: 1px solid #ccc!important" class="small-5 columns">
                            <ul id="unUsedNames" style="list-style-type: none" class="side-nav">
                                <%
                                    ArrayList<Locker> lockerList = lockerCtrl.getLockersWithPeopleInNeighbourhood(currentUser.getNeighbourhood());
                                    DemographicsCSVController demoCtrl = new DemographicsCSVController();
                                    ArrayList<Demographics> demoList = demoCtrl.getUsersByNeighbourHood(currentUser.getNeighbourhood());
                                    ArrayList<String> listOfPeopleWithLockerSid = new ArrayList<String>();
                                    for (Locker l : lockerList) {
                                        listOfPeopleWithLockerSid.add(l.getTaken_by());
                                    }
                                    //ArrayList<Demographics> users = demo.getUsersByNeighbourHood(currentUser.getNeighbourhood());
                                    // Only displays people names whose role = USERS; 
                                    // Iterates through a array of SIDs
                                    // Displays SIDs not found in the <String SID, Locker l> Mapping
                                    for (Demographics d : demoList) {
                                        if (!listOfPeopleWithLockerSid.contains(d.getSid())) {
                                            String sid = d.getSid();
                                            String name = d.getName();

                                %> <li name='<%=sid%>' id='<%= sid%>' class='unUsedNamesPoint' onclick='unUsedBold(this)'><%= name%></li> 

                                <%
                                        }
                                    }

                                %>
                            </ul>
                        </div>
                        <div class="small-2 columns">
                            <div class="row"></div>
                            <input type="button" name="buttonAddAll" class="button small expand radius" onclick="addAllNames()" value="Assign All"> 
                            <input type="button" name="buttonAddSelected" class="button small expand radius" onclick="addSelectedNames()" value="Assign Selected"> 
                            <div style="padding-bottom: 30px" class="row"></div>
                            <input type="button" name="buttonRemoveAll" class="button small expand radius" onclick="removeAllNames()" value="Remove All" >
                            <input type="button" name="buttonRemoveSelected" class="button small expand radius" onclick="removeSelectedNames()" value="Remove Selected">
                        </div>
                        <div style="overflow: scroll; height: 300px; border: 1px solid #ccc!important" class="small-5 columns">
                            <ul id="usedNames" style="list-style-type:none" class="side-nav"></ul>
                        </div>
                    </div>
                </div>

                <div class="small-4 columns">

                    <input type="hidden" name="nb" value="<%=currentUser.getNeighbourhood()%>">
                    <label style="padding-top: 20px"><strong>Select Locker Cluster To Assign</strong>
                        <select name="lockerCluster" required>
                            <%
                                LockerController locker_ctrl = new LockerController();
                                HashMap<String, ArrayList<Locker>> lockerMap = locker_ctrl.getLockerClusterListByNeighbourhood(currentUser.getNeighbourhood());
                                for (Map.Entry<String, ArrayList<Locker>> entry : lockerMap.entrySet()) {
                                    String key = (String) entry.getKey();
                                    ArrayList<Locker> value = (ArrayList<Locker>) entry.getValue();
                                    int count = 0;
                                    for (Locker l : value) {
                                        if (l.getTaken_by() == null) {
                                            count++;
                                        }
                                    }
                            %>
                            <option value="<%=key%>"><%=key%> - <%=count%> available lockers</option> 
                            <!--<option value="ox">ox</option> 
                            <option value="tiger">tiger</option>
                            <option value="rabbit">rabbit</option>
                            <option value="dragon">dragon</option>
                            <option value="snake">snake</option>
                            <option value="horse">horse</option>
                            <option value="sheep">sheep</option>
                            <option value="monkey">monkey</option>
                            <option value="rooster">rooster</option>
                            <option value="dog">dog</option>
                            <option value="pig">pig</option>-->
                            <%
                                }
                            %>
                        </select>
                    </label>
                    <!--Submit-->
                    <input type="submit" value="Random Assign" class="button sloca normal radius"/>

                </div>
                <hr>
            </form>
        </div>
        <div style="padding-left: 30px; padding-right: 30px; padding-bottom: 30px;">
            <img src="img/FloorLayout.png" style="border: 3px solid #ccc!important">
        </div>
        <%
            //for (int i = 1; i <= clusterNo; i++) {
        %>
        <div class="row" name="displayLocker" id="displayLocker">
            <!--<div class="medium-8 columns">-->
            <!--<h5> Choose locker by clicking the corresponding locker in the layout below:</h5>
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
            </div>-->
        </div>
        <% //}%>

        <!-- Included JS Files (Compressed) -->
        <script src="js/vendor/jquery.js"></script>
        <script src="js/datetime/foundation.min.js" type="text/javascript"></script>
        <script src="js/jquery-3.2.1.min.js" type="text/javascript"></script>


        <!-- this is the javascript for datetimepicker -->
        <script src="js/datetime/jquery.datetimepicker.js" type="text/javascript"></script>
        <!-- end of the javascript for datetimepicker -->

        <!-- Initialize JS Plugins -->
        <script src="js/datetime/app.js" type="text/javascript"></script>

        <script src="js/foundation.min.js"></script>
        <script>
                                $(document).foundation();
                                //Case II: If already booked
                                var bookedSeats = [5, 10, 25];
                                //var bookedSeats = [];
                                var countCluster = document.getElementById('noOfCluster1').value;
                                var initialiseLocker = function (i, key) {
                                    //bookedSeats = [];
                                    /*$("input[name='lockerNo" + i + "']").each(function () {
                                     value = $(this).val();
                                     bookedSeats.push(parseInt(value.substring(1)));
                                     });*/
                                    if (key == "rat") {
                                        setting = settings1;
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

                                    var displayLocker = "<row><div class='medium-8 columns'>" +
                                            "<h5> Cluster " + key + ": </h5>" +
                                            "<div id='holder'> <ul id='place'></ul>    </div>" +
                                            "<div style='float:left;'>" +
                                            "<ul id='seatDescription'>" +
                                            "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_in-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Available Locker</li>" +
                                            "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_out-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Booked Locker</li>" +
                                            "<li style='background:url(\"https://maxcdn.icons8.com/Color/PNG/24/Finance/safe_ok-24.png\") no-repeat scroll 0 0 transparent; padding-right: 30px'>Selected Locker</li>" +
                                            "</ul>" +
                                            "</div></row>";


                                    $('#displayLocker').html(displayLocker);


                                    //return bookedSeats;
                                };
                                //$(document).foundation();
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
                                    selectingSeatCss: 'selectingSeat'
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
                                    selectingSeatCss: 'selectingSeat'
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
                                //case I: Show from starting
                                //init();
                                var init = function (reservedSeat) {
                                    //for (a = 1; a <= 12; a++) {
                                    if (document.getElementById('cluster1') != null) {
                                        reservedSeat = initialiseLocker(1, document.getElementById("cluster1").value);
                                        var str = [], seatNo, className;
                                        for (i = 0; i < settings.rows; i++) {
                                            for (j = 0; j < settings.cols; j++) {
                                                seatNo = (i + j * settings.rows + 1);
                                                className = settings.seatCss + ' ' + settings.rowCssPrefix + i.toString() + ' ' + settings.colCssPrefix + j.toString();
                                                if ($.isArray(reservedSeat) && $.inArray(seatNo, reservedSeat) != -1) {

                                                } else {
                                                    className += ' ' + settings.selectedSeatCss;
                                                }
                                                str.push('<li class="' + className + '"' +
                                                        'style="top:' + (i * settings.seatHeight).toString() + 'px;left:' + (j * settings.seatWidth).toString() + 'px;">' +
                                                        '<a style="color: white; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black; font-size: 15px" title="' + seatNo + '">' + seatNo + '</a>' +
                                                        '</li>');
                                            }
                                        }
                                        $('#place').html(str.join(''));
                                    }
                                };
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

                                function loadNames() {

                                    var names = ["Barnaby", "Marmaduke", "Aloysius", "Benjy", "Cobweb", "Dartagnan", "Egbert", "Felix", "Gaspar", "Humbert", "Ignatius", "Jayden", "Kasper", "Leroy", "Maximilian", "Neddy", "Obiajulu", "Pepin", "Quilliam", "Rosencrantz", "Sexton", "Teddy", "Upwood", "Vivatma", "Wayland", "Xylon", "Yardley", "Zachary", "Usansky", "John", "Jacob", "Jingleheimer", "Schmidt", "Super", "Cali", "Fragilistic", "Expiali", "Docious"];
                                    refreshUnAssignList(names);

                                }
                                function unUsedBold(li) {
                                    if ($(li).hasClass("unUsedBold")) {
                                        $(li).removeClass("unUsedBold");
                                    } else {
                                        $(li).addClass("unUsedBold");
                                    }
                                }


                                function addAllNames() {

                                    var unusedNamesList = document.getElementsByClassName("unUsedNamesPoint");
                                    var count = unusedNamesList.length;
                                    var name;
                                    var sid;
                                    var temp_sid = [];
                                    var temp_name = [];
                                    while (count > 0) {
                                        // each element in unusedNamesList as a child node called text node with a value
                                        name = unusedNamesList[0].childNodes[0].nodeValue;
                                        temp_name.push(name);
                                        sid = unusedNamesList[0].getAttribute("id");
                                        temp_sid.push(sid);
                                        unusedNamesList[0].className = unusedNamesList[0].className.replace("unUsedNamesPoint", "usedNamesPoint");
                                        count--;
                                    }
                                    $("ul#unUsedNames li").remove();
                                    refreshAssignList(temp_name, temp_sid);
                                }

                                function removeAllNames() {

                                    var usedNamesList = document.getElementsByClassName("usedNamesPoint");
                                    var count = usedNamesList.length;
                                    var name;
                                    var sid;
                                    var temp_name = [];
                                    var temp_sid = [];
                                    while (count > 0) {
                                        // each element in unusedNamesList as a child node called text node with a value
                                        name = usedNamesList[0].childNodes[0].nodeValue;
                                        sid = usedNamesList[0].childNodes[1].getAttribute("value");
                                        temp_name.push(name);
                                        temp_sid.push(sid);
                                        usedNamesList[0].className = usedNamesList[0].className.replace("usedNamesPoint", "unUsedNamesPoint");
                                        count--;
                                    }
                                    $("ul#usedNames li").remove();
                                    refreshUnassignList(temp_name, temp_sid);
                                }


                                function refreshUnassignList(temp_name, temp_sid) {

                                    var unUsedNamesList = document.getElementsByClassName("unUsedNamesPoint");
                                    var count = unUsedNamesList.length;
                                    var sid;
                                    var name;
                                    for (var j = 0; j < count; j++) {
                                        // each element in unusedNamesList as a child node called text node with a value
                                        sid = unUsedNamesList[j].getAttribute("id");
                                        name = unUsedNamesList[j].childNodes[0].nodeValue;
                                        temp_name.push(name);
                                        temp_sid.push(sid);
                                    }
                                    $("ul#unUsedNames li").remove();
                                    // import all unselected names
                                    for (i = 0; i < temp_name.length; i++) {
                                        var name = temp_name[i];
                                        var ID = temp_sid[i]; // change later to id from demographics
                                        var newListElement = document.createElement("li");
                                        newListElement.setAttribute("id", ID);
                                        newListElement.setAttribute("name", ID);
                                        newListElement.className = "unUsedNamesPoint";
                                        newListElement.addEventListener('click', (function (e)
                                        {
                                            e.preventDefault();
                                            if ($(this).hasClass("unUsedBold")) {
                                                $(this).removeClass("unUsedBold");
                                            } else {
                                                $(this).addClass("unUsedBold");
                                            }
                                        })
                                                );

                                        var newTextNode = document.createTextNode(name);
                                        newListElement.appendChild(newTextNode); //add the text node to the newly created div. 

                                        // add the newly created element and its content into the DOM
                                        var usedNames = document.getElementById("unUsedNames");
                                        usedNames.appendChild(newListElement);
                                    }
                                    addSelectedNames();
                                }

                                function refreshAssignList(temp_name, temp_sid) {
                                    var usedNamesList = document.getElementsByClassName("usedNamesPoint");
                                    var count = usedNamesList.length;
                                    var sid;
                                    var name;
                                    if (count != 0) {
                                        for (var j = 0; j < count; j++) {
                                            // each element in unusedNamesList as a child node called text node with a value
                                            name = usedNamesList[j].childNodes[0].nodeValue;
                                            sid = usedNamesList[j].childNodes[1].getAttribute("value");  //*********need to change this
                                            temp_name.push(name);
                                            temp_sid.push(sid);
                                        }
                                    }
                                    $("ul#usedNames li").remove();
                                    // import all unselected names
                                    for (var i = 0; i < temp_name.length; i++) {
                                        var name = temp_name[i];
                                        var SID = temp_sid[i];
                                        //var ID = "list_".concat(name);
                                        var newListElement = document.createElement("li");
                                        newListElement.setAttribute("id", SID);
                                        newListElement.className = "usedNamesPoint";
                                        newListElement.addEventListener('click', (function (e)
                                        {
                                            e.preventDefault();
                                            if ($(this).hasClass("usedBold")) {
                                                $(this).removeClass("usedBold");
                                            } else {
                                                $(this).addClass("usedBold");
                                            }
                                        })
                                                );

                                        var newTextNode = document.createTextNode(name);
                                        newListElement.appendChild(newTextNode); //add the text node to the newly created div. 

                                        // =============== Only for assigned names ===========//
                                        var input = document.createElement("input");
                                        input.type = "hidden";
                                        input.name = i + 1; // set the input name
                                        input.value = SID; // set input value ,********* change to SID later
                                        newListElement.appendChild(input);

                                        // add the newly created element and its content into the DOM
                                        var usedNames = document.getElementById("usedNames");
                                        usedNames.appendChild(newListElement);
                                    }
                                }

                                function addSelectedNames() {
                                    var selectedNamesList = document.getElementsByClassName("unUsedNamesPoint unUsedBold");
                                    var count = selectedNamesList.length;
                                    var name;
                                    var sid;
                                    var temp_name = [];
                                    var temp_sid = [];
                                    while (count > 0) {
                                        // each element in unusedNamesList as a child node called text node with a value
                                        name = selectedNamesList[0].childNodes[0].nodeValue;
                                        sid = selectedNamesList[0].getAttribute("id");
                                        temp_name.push(name);
                                        temp_sid.push(sid);
                                        selectedNamesList[0].className = selectedNamesList[0].className.replace("unUsedNamesPoint", "usedNamesPoint");
                                        count--;
                                    }
                                    $("li").remove(".unUsedBold");
                                    refreshAssignList(temp_name, temp_sid);
                                }




                                function removeSelectedNames() {
                                    var selectedNamesList = document.getElementsByClassName("usedNamesPoint usedBold");
                                    var count = selectedNamesList.length;
                                    var name;
                                    var ID;
                                    var temp_sid = [];
                                    var temp_name = [];
                                    while (count > 0) {
                                        // each element in unusedNamesList as a child node called text node with a value
                                        name = selectedNamesList[0].childNodes[0].nodeValue;
                                        ID = selectedNamesList[0].childNodes[1].getAttribute("value");
                                        temp_name.push(name);
                                        temp_sid.push(ID);
                                        selectedNamesList[0].className = selectedNamesList[0].className.replace("usedNamesPoint", "unUsedNamesPoint");
                                        count--;
                                    }
                                    $("li").remove(".usedBold");
                                    refreshUnassignList(temp_name, temp_sid);
                                }
                                /*
                                 function removeSelectedNames() {
                                 var selectedNamesList = document.getElementsByClassName("usedNamesPoint usedBold");
                                 var count = selectedNamesList.length;
                                 var name;
                                 var temp_name = [];
                                 while (count > 0) {
                                 // each element in unusedNamesList as a child node called text node with a value
                                 name = selectedNamesList[0].childNodes[0].nodeValue;
                                 temp_name.push(name);
                                 selectedNamesList[0].className = selectedNamesList[0].className.replace("usedNamesPoint", "unUsedNamesPoint");
                                 count--;
                                 }
                                 $("li").remove(".usedBold");
                                 refreshUnassignList(temp_name);
                                 }
                                 */

        </script>
    </body>
</html>
