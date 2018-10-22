<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="i18n" uri="/taglib/i18n" %>
<!DOCTYPE html>
<html lang="en">
<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.i18n.UIMessageHelper" %>
<head>
    <title>Login</title>

    <script src="./js/popper.js/dist/umd/popper.min.js"></script>
    <script src="./js/tether/dist/js/tether.min.js"></script>
    <link href="./js/tether/dist/css/tether.min.css" rel="stylesheet"/>

    <script src="./js/axios/dist/axios.min.js"></script>

    <script src="./js/jquery/dist/jquery.slim.min.js"></script>
    <script src="./js/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
    <link href="./js/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="./js/font-awesome/css/font-awesome.min.css">

    <script type="text/javascript">
        function doLogin() {
            axios({
                method: 'post',
                url: '/api/auth/login',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: "email=" + document.getElementById('email').value + "&pwd=" + document.getElementById('pwd').value
            }).then(function (response) {
                if (response.data.success === true) {
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
                    document.getElementById('login_result').innerHTML = '<%=UIMessageHelper.getInstance().getLocalizedMsg("login.fail")%>';
                }
            }).catch(function(error) {
                document.getElementById('login_result').innerHTML = error.response.data.message;
            });
            return false;
        }
    </script>
</head>
<body background="./static/images/fjord_big.jpg">
<form class="form-horizontal" role="form" id="form0">
    <table style="width:400px; float: none" cellspacing="4">
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <th style="text-align: right"><i18n:msg key="login.text.email"/></th>
            <td><input style="width:200px" id="email" name="email" type="text" title="<i18n:msg key="login.text.email"/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right"><i18n:msg key="login.text.password"/></th>
            <td><input style="width:200px" id="pwd" name="pwd" type="password" title="<i18n:msg key="login.text.password"/>"/></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" style="text-align: center">
                <button class="btn btn-default" style="float: none" onclick="return doLogin()"><i class="fa fa-sign-in"></i> <i18n:msg key="login.button"/></button><br/>
                <span id="login_result"></span>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
