<%-- 
    Document   : manager
    Created on : Jun 12, 2017, 9:50:36 AM
    Author     : Default
--%>

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
            <div>
                <div class="row">
                    <div style="overflow: scroll; height: 300px" class="small-5 columns">
                        <ul id="unUsedNames" style="list-style-type:none" class="side-nav"></ul>
                    </div>
                    <div class="small-2 columns">
                        <div class="row"></div>
                        <button name="buttonAddAll" class="button sloca normal radius" onclick="addAllNames()">Assign All</button>
                        <button name="buttonAddSelected" class="button sloca normal radius" onclick="addSelectedNames()">Assign Selected</button>
                        <div class="row"></div>
                        <button name="buttonRemoveAll" class="button sloca normal radius" onclick="removeAllNames()">Remove All</button>
                        <button name="buttonRemoveSelected" class="button sloca normal radius" onclick="removeSelectedNames()">Remove Selected</button>
                    </div>
                    <div style="overflow: scroll; height: 300px" class="small-5 columns">
                        <ul id="usedNames" style="list-style-type:none" class="side-nav"></ul>
                    </div>
                </div>
            </div>


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
                <%                    // }
                    // }
                %>
                <form action="" method="POST">
                    <label><strong>Floor</strong>
                        <select name="floor" required>
                            <%                                //Codes here to ensure whatever the user has selected before is selected again - to be more userfriendly
                            %>
                            <option value="5">Level 5</option> 
                            <option value="6">Level 6</option> 
                            <option value="7">Level 7</option>  
                        </select>
                    </label>

                    <label><strong>Locker Cluster</strong>
                        <select name="lockerCluster" required>
                            <%                                //Codes here to ensure whatever the user has selected before is selected again - to be more userfriendly
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

            <%                //There are results
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
            <%                            //retrieves results from the hashmap
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
            <%                //    }
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
                            var namesOff = "namesOff";
                            var namesOn = "namesOn";

                            function loadNames() {
                                var names = ["John", "Jacob", "Jingleheimer", "Schmidt"];
                                refreshUnassignList(names);
                                var names2 = ["Super", "Cali", "Fragilistic", "Expiali", "Docious"];
                                refreshAssignList(names2);
                            }


                            function addAllNames() {

                                var unusedNamesList = document.getElementsByClassName("unUsedNamesPoint");
                                var count = unusedNamesList.length;
                                var name;
                                var temp_name = [];
                                while (count > 0) {
                                    // each element in unusedNamesList as a child node called text node with a value
                                    name = unusedNamesList[0].childNodes[0].nodeValue;
                                    temp_name.push(name);                                    
                                    unusedNamesList[0].className = unusedNamesList[0].className.replace("unUsedNamesPoint", "usedNamesPoint");
                                    count--;
                                }
                                $("ul#unUsedNames li").remove();
                                refreshAssignList(temp_name);
                            }
                            
                            function removeAllNames() {

                                var usedNamesList = document.getElementsByClassName("usedNamesPoint");
                                var count = usedNamesList.length;
                                var name;
                                var temp_name = [];
                                while (count > 0) {
                                    // each element in unusedNamesList as a child node called text node with a value
                                    name = usedNamesList[0].childNodes[0].nodeValue;
                                    temp_name.push(name);                                    
                                    usedNamesList[0].className = usedNamesList[0].className.replace("usedNamesPoint", "unUsedNamesPoint");
                                    count--;
                                }
                                $("ul#usedNames li").remove();
                                refreshUnassignList(temp_name);
                            }
                            
                            function refreshUnassignList(temp_name){
                                
                                // import all unselected names
                                for (i = 0; i < temp_name.length; i++) {
                                    var name = temp_name[i];
                                    var ID = "list_".concat(name);  // change later to id from demographics
                                    var newListElement = document.createElement("li");
                                    newListElement.setAttribute("id", ID);
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
                                    //newListElement.className = "namesOff";
                                    newListElement.appendChild(newTextNode); //add the text node to the newly created div. 

                                    // add the newly created element and its content into the DOM
                                    var usedNames = document.getElementById("unUsedNames");
                                    usedNames.appendChild(newListElement);
                                }
                            }
                            function refreshAssignList(temp_name){
                                
                                // import all unselected names
                                for (i = 0; i < temp_name.length; i++) {
                                    var name = temp_name[i];
                                    var ID = "list_".concat(name);  // change later to id from demographics
                                    var newListElement = document.createElement("li");
                                    newListElement.setAttribute("id", ID);
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
                                    //newListElement.className = "namesOff";
                                    newListElement.appendChild(newTextNode); //add the text node to the newly created div. 

                                    // add the newly created element and its content into the DOM
                                    var usedNames = document.getElementById("usedNames");
                                    usedNames.appendChild(newListElement);
                                }
                            }

                            function addSelectedNames() {
                                var selectedNamesList = document.getElementsByClassName("unUsedNamesPoint unUsedBold");
                                var count = selectedNamesList.length;
                                var name;
                                var temp_name = [];
                                while (count > 0) {
                                    // each element in unusedNamesList as a child node called text node with a value
                                    name = selectedNamesList[0].childNodes[0].nodeValue;
                                    temp_name.push(name);                                    
                                    selectedNamesList[0].className = selectedNamesList[0].className.replace("unUsedNamesPoint", "usedNamesPoint");
                                    count--;
                                }
                                $("li").remove(".unUsedBold");
                                refreshAssignList(temp_name);
                            }




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
                                loadNames();

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

                            /*
                             var $names = $('.namesOff').click(function (e) {
                             e.preventDefault();
                             $names.removeClass(namesOn);
                             $(this).addClass(namesOn);
                             });
                             */
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
