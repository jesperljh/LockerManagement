<%-- 
    Document   : topbar.jsp
    Created on : Sep 11, 2014, 12:58:38 AM
    Author     : Eugene/Kenneth
--%>

<nav class="top-bar toggle-topbar" data-topbar>
    <ul class="title-area">
        <li class="name">
            <h1><a href="heatmap.jsp">&nbspJP<i class="fi-clock"></i>Intern</a></h1>
        </li>
    </ul>

    <section class="top-bar-section">

        <ul class="left">
            <li class="divider"></li>
            <li><a href="heatmap.jsp"><i class="fi-cloud"></i> Locker</a></li>
            <li class="divider"></li>
            <li class="has-dropdown not-click">
                <a href="#"><i class="fi-book-bookmark"></i>&nbspReports</a>
                
                <ul class="dropdown">
                    <li><label>Basic Location Report</label></li>
                    <li><a href="basiclocreport.jsp"><i class="fi-results"></i>&nbspBreakdown by Year, Gender & School</a></li>
                    <li><a href="topkpopular.jsp"><i class="fi-marker"></i>&nbspTop-K Popular Places</a></li>
                    <li><a href="topkcompanion.jsp"><i class="fi-torsos-male-female"></i>&nbspTop-K Companions</a></li>
                    <li><a href="topknextplaces.jsp"><i class="fi-arrow-right"></i>&nbspTop-K Next Places</a></li>
                    <li><label>Group-Aware Location Reports</label></li>
                    <li><a href="grouptopkpopular.jsp"><i class="fi-arrows-in"></i>&nbspGroup Top-K Popular Places</a></li>
                    <li><a href="grouptopknextplaces.jsp"><i class="fi-arrows-out"></i>&nbspGroup Top-K Next Places</a></li>
                </ul>
            <li class="divider"></li>
            <li><a href="automaticgroupdetection.jsp"><i class="fi-torsos-all"></i>&nbspAutomatic Group Detection</a></li>
        </ul>

        <ul class="right">
            <li><a href="#" data-reveal-id="revealModal" class="button round"><i class="fi-torso"></i><u style="text-decoration: none">&nbsp<%=currentUser.getName()%></u></a></button></li> 
            <div id="revealModal" class="reveal-modal" data-reveal> 
                <h1><i class="fi-torso"></i>&nbsp<%=currentUser.getName()%></h1> 
                <p class="lead"><strong>MAC Address:</strong>&nbsp<%=currentUser.getMacAddress()%></p> 
                <p class="lead"><strong>Gender:</strong>&nbsp
                    <%
                        if (currentUser.getGender().toUpperCase().equals("M")) {
                    %>
                    <i class="fi-male-symbol"></i>
                    <%
                    } else {
                    %>
                    <i class="fi-female-symbol"></i>
                    <%
                        }
                    %>
                </p>
                <p class="lead"><strong>Email:</strong>&nbsp<%= currentUser.getEmail()%></label>

                <a class="close-reveal-modal">&#215;</a> </div>
            <li><a href="logout.jsp"><i class="fi-paw"></i>&nbspLog Out</a></li>
        </ul>
    </section>
</nav>
<script src="js/vendor/jquery.js"></script>
<script src="js/foundation.min.js"></script>
<script>
    $(document).foundation();
</script>
