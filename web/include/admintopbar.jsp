<%-- 
    Document   : admintopbar
    Created on : Sep 11, 2014, 3:32:04 PM
    Author     : Jesper
--%>
<nav class="top-bar" data-topbar>
    <ul class="title-area">

        <li class="name">
            <h1>
                <a href="bootstrap.jsp">  
                    &nbspSL<i class="fi-target-two"></i>CA</a>
                </a>
            </h1>
        </li>
    </ul>

    <section class="top-bar-section">
        <ul class="left">
            <li class="divider"></li>
            <li class="has-dropdown"> 
                <!--<a href="bootstrap.jsp"><i class="fi-upload"></i>  Bootstrap</a>
                <ul class="dropdown">
                    <li><a href="uploadcsv.jsp"><i class="fi-plus"></i>   Upload Additional Files</a></li>
                </ul>-->  
            <li class="divider"></li>
        </ul>
        <ul class="right">
  <li><a href="#" data-reveal-id="revealModal"><i class="fi-torso"></i>   <u>Welcome, <%=currentUser.getName()%></u></a></li> 
            <div id="revealModal" class="reveal-modal" data-reveal> 
                <h1><%=currentUser.getName()%></h1> 
                <p class="lead"><strong>MAC Address: </strong><%=currentUser.getMacAddress()%></p> 
                <label>
                    <strong>Gender: </strong><%= currentUser.getGender()%> 
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
                </label>
                    <label><strong>Email: </strong><%= currentUser.getSid()%></label>

                <a class="close-reveal-modal">&#215;</a> </div>

            <li class="divider"></li>
            <li><a href="logout.jsp">Log Out</a></li>
        </ul>
    </section>
</nav>

