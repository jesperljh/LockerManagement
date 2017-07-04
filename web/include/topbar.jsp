<%-- 
    Document   : topbar.jsp
    Created on : Sep 11, 2014, 12:58:38 AM
    Author     : Jesper
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
            <!--<li class="has-dropdown not-click"><a href="#"><i class="fi-cloud"></i> Locker</a></li>-->
            <li class="divider"></li>
            <li class="has-dropdown not-click">
                <a href="#"><i class="fi-cloud"></i>&nbspLocker</a>
                <ul class="dropdown">
                    <li><label>Locker Assignment</label></li>
                    <li><a href="manager.jsp"><i class="fi-results"></i>&nbspAssign Locker To User</a></li>
                    <li><a href="managerRemoveUser.jsp"><i class="fi-marker"></i>&nbspUnassign Locker To User</a></li>
                    <li><a href="managerRegisterUser.jsp"><i class="fi-paw"></i>&nbspRegister User</a></li>
                </ul>
            <li class="divider"></li>
            <!--<li><a href="automaticgroupdetection.jsp"><i class="fi-torsos-all"></i>&nbspAutomatic Group Detection</a></li>-->
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
                <p class="lead"><strong>Email:</strong>&nbsp<%= currentUser.getSid()%></label>

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
