<%@ page import="org.achacha.webcardgame.web.vue.VueHelper" %>
<script type="application/javascript">
    var homeApp = new Vue({
        el: '#admin-home-app',
        data: {
            app: <%=VueHelper.buildData().toString()%>,
            page: null
        }
    });

    console.log(homeApp);
</script>