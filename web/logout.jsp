<%-- 
    Document   : logout
    Created on : Sep 22, 2014, 7:09:29 PM
    Author     : Jesper
--%>
<%
    session.invalidate();
    response.sendRedirect("login.jsp");
%>

