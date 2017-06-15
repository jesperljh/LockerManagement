<%-- 
    Document   : index
    Created on : Sep 10, 2014, 12:20:37 AM
    Author     : Jesper
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="entity.Demographics"%>
<%@page import="java.util.HashMap"%>
<%@page import="controller.LoginController"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    //Declare variables
    String loginStatus = "";
    String username = "";
    String password = "";

    //Declare loginResult hashmap
    HashMap<String, Object> loginResult = null;
    if (request.getMethod() != null && request.getMethod().equals("POST")) {
        //Get the user input
        username = request.getParameter("username");
        password = request.getParameter("password");

        //Declare variable token
        String token;

        //Declare and initialize loginController
        LoginController loginController = new LoginController();
        //Authenticate user credentials
        loginResult = loginController.authenticateUser(username, password);

        //Successful results
        if (loginResult.get("status").equals("success")) {
            token = (String) loginResult.get("token");
            Demographics demographics = (Demographics) loginResult.get("user");
            session.setAttribute("token", token);
            session.setAttribute("user", demographics);

            //If user is an admin
            if (demographics.getName().equals("admin")) {
                response.sendRedirect("admin.jsp");
            } else {
                //If user is student
                response.sendRedirect("user.jsp");
            }
            return;
        }
        loginStatus = "fail";
    }

%>
<!DOCTYPE html>
<html>
    <head>
        <!--Google Font-->
        <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700,800' rel='stylesheet' type='text/css'>        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA | Login</title>
        <link rel="stylesheet" href="css/foundation.css" />
        <script src="js/vendor/modernizr.js"></script>
        <!--Icons-->
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.css">
        <link rel="stylesheet" type="text/css" href="css/foundation-icons/foundation-icons.svg">

    </head>
    <body>
        <!--JPMorgan Image-->
        <div class="row">
                 <div class="small-7 small-centered column" style="padding:10px">
                    <img src="img/J_P_Morgan_Chase_Logo_2008_1.svg.png" alt="J_P_Morgan_Chase_Logo_2008_1" style="padding-top: 60px"/>
                </div>
        </div>

        <!--Alert box-->
        <div class="row">
            <div class="small-7 small-centered column" style = "padding:10px">
                <form method="POST">
                    <%  //Fail login, display error message                              
                        if (loginStatus.equals("fail")) {
                    %>
                    <div data-alert class="alert-box alert radius">
                        <%
                            if (loginResult != null) {
                        %>
                        <center>Invalid Username / Password</center>
                        <%
                            }
                        %>
                    </div>
                    <%
                        }
                    %>
            </div>
                    <!--Display Username Textbox-->
                    <div class="row">
                    <div class="small-7 small-centered column panel radius">
                        <label><strong>Username<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                            <div class="row collapse">
                                <div class="small-2 columns">
                                    <span class="prefix radius"><i class="fi-torso size-24"></i></span>
                                </div>
                                <div class="small-10 columns">
                                    <input type="text" name="username" placeholder="Enter your SID (F123456)" value="<%=username%>" required>

                                </div>
                            </div> 
                        </label>

                        <!--Display Password Textbox-->             
                        <label><strong>Password<sup><small style="color:red; padding: 0px 5px">**Required </small></sup></strong>
                            <div class="row collapse">
                                <div class="small-2 columns">
                                    <span class="prefix radius"><i class="fi-lock size-24"></i></span>
                                </div>
                                <div class="small-10 columns">
                                    <input type="password" name="password" placeholder="Enter your password" required>
                                </div>
                            </div> 
                        </label>
                        <!--Display Submit Button-->
                        <center><input type="submit" class="button sloca normal radius expand" value="Login"></center>
                    </div>
                </form>
            </div>
        </div>

        <script src="js/vendor/jquery.js"></script>
        <script src="js/foundation.min.js"></script>
        <script>
            $(document).foundation();
        </script>
    </body>
</html>
