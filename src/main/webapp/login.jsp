<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<%@ page import="org.achacha.base.context.CallContext" %>
<head>
    <title>Title</title>
    <script src="./js/jquery/dist/jquery.js" type="application/javascript"></script>

    <link href="./js/tether/dist/css/tether.min.css" rel="stylesheet"/>
    <script src="./js/tether/dist/js/tether.min.js"></script>
    <link href="./js/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <script src="./js/bootstrap/dist/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="./js/font-awesome/css/font-awesome.min.css">
    <link rel="shortcut icon" href="./static/images/fjord_logo.jpg" type="image/x-icon" />

    <script src="./js/requirejs/require.js"></script>
    <script type="text/javascript">
        function doLogin() {
            $('#login_result').html('');
            $.ajax(
                {
                    type: 'POST',
                    url: '/api/auth/login',
                    data: {"email":$('#email').val(), "pwd":$('#password').val()},
                    timeout: 600000,
                    success: function (result) {
                        if (result.success) {
                            <%
                                if (null != session) {
                                    String from = (String)session.getAttribute(CallContext.SESSION_REDIRECT_FROM);
                                    if (null != from)
                                        out.print("window.location = '"+from+"';");
                                    else
                                        out.print("window.location = 'index.jsp';");
                                }
                                else
                                    out.print("window.location = 'index.jsp';");
                            %>
                        }
                        else {
                            console.log("Result fail on  login");
                        }
                    },
                    error: function (result) {
                        console.log("Error on login submit: "+result);
                    }
                });

            return false;
        }
    </script>
</head>
<body>
<form class="form-horizontal" role="form">
    <table style="width:400px; float: none" cellspacing="4">
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <th style="text-align: right">E-Mail</th>
            <td><input style="width:200px" id="email" name="email" type="text" placeholder="E-Mail"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Password</th>
            <td><input style="width:200px" id="password" name="pwd" type="password" placeholder="Password"/></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" style="text-align: center"><button class="btn btn-default" style="float: none" onclick="return doLogin()"><i class="fa fa-sign-in"></i> Login</button></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr><td colspan="2"><span id="login_result"></span></td></tr>
    </table>
</form>
</body>
</html>
