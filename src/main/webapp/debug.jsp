<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Debug</title>
    <script src="js/axios/dist/axios.min.js"></script>
<script>
    async function getLoginName() {
        let responseData = {
            success: false,
            state: "Unfinished"
        };

        await axios.get('/api/auth/login')
            .then(function (response) {
                if (response.data.success === true) {
                    console.log("SUCCESS: "+JSON.stringify(response.data.user));
                    responseData['user'] = response.data.user;
                }
                else {
                    console.log("FAIL: "+response);
                    responseData['state'] = response;
                }
            }).catch(function(error) {
                console.log("ERROR: "+error);
                responseData['state'] = error;
            });

        console.log("responseData="+JSON.stringify(responseData));

        document.getElementById('output').innerHTML = JSON.stringify(responseData);

        return responseData;
    }
</script>
</head>
<body>
<button onclick="getLoginName()">Get Login Name</button><br/><pre id="output"></pre>

<%--<jsp:useBean id="global" scope="request" class="org.achacha.base.global.Global"/>--%>
<%--<%--%>
    <%--out.print("Debug");--%>
<%--%>--%>


</body>
</html>
                                                                                                                    
