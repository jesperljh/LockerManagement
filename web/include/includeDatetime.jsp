<%-- 
    Document   : includeDatetime
    Created on : Oct 13, 2014, 4:37:21 PM
    Author     : Kenneth
--%>
<%
    //This is not a POST, let's check if there is a datetime in the session attribute.
    if (request.getMethod() != null && request.getMethod().equals("GET")) {
        //Retrieve datetimeInput from session
        String datetimeInput = (String) session.getAttribute("datetimeInput");

        //If datetimeInput is not null
        if (datetimeInput != null) {
            //Set the value into dateTime so it can be displayed
            dateTime = datetimeInput;
%>               
<div data-alert class="alert-box info radius rounded">
    <center>
    Auto-populated Date: <strong><%= dateTime.replace('T', ' ')%></strong>
    </center>
</div>              
<%
        }
    }
%>