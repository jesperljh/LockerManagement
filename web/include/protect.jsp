<%-- 
    Document   : protect
    Created on : Oct 6, 2014, 1:50:18 PM
    Author     : Jesper
--%>
<%@page import="utility.SharedKey"%>
<%@page import="is203.JWTUtility"%>
<%@page import="utility.TokenValidation"%>
<%@page import="entity.Demographics"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Demographics currentUser = (Demographics) session.getAttribute("user");
    
    
    if (currentUser == null || currentUser.getSid().equals("admin") ) {
        response.sendRedirect("login.jsp");
        return;
    }    
    //Check for token    
    String checkToken = (String) session.getAttribute("token");
    if (!TokenValidation.validateToken(checkToken)) {
        //If token has somehow expired, let's reassign the user a new token! :)
        String username=currentUser.getName();
        session.setAttribute("token", JWTUtility.sign(SharedKey.getKey(),username));
    }
%>