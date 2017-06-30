<%-- 
    Document   : processRequest
    Created on : Jun 29, 2017, 2:46:27 PM
    Author     : Default
--%>

<%@page import="controller.RequestController"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            RequestController requestCtrl = new RequestController();
            String action = request.getParameter("request");
            int id = Integer.parseInt(request.getParameter("id"));
            String mySid = request.getParameter("mySid");
            String rSid = request.getParameter("rSid");
            if (action != null && action.equals("accept")) {
                requestCtrl.acceptRequest(id, mySid, rSid);
            } else if (action != null && action.equals("reject")) {
                requestCtrl.rejectRequest(id, mySid);
            }
            response.sendRedirect("user.jsp");
            %>
        <h1>Hello World!</h1>
    </body>
</html>
