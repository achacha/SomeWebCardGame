<%@ page import="org.achacha.webcardgame.web.vue.VueHelper" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>setNavbarHeader('DaoViewer')</script>

<div id="admin-home-app">
    <object-picker></object-picker>
</div>

</body>
<jsp:include page="_admin_footer.jsp"/>
<script type="application/javascript">
    Vue.component('object-picker', {
        data: function(){ return {
            availableDbos: [],
            objectClass: null,
            objectId: null,
            objectData: "",
            errorMessage: null
        }},
        template: `<div>
<table class="table table-bordered" width="100%"><tr>
<th width="25%">
    <span>Dbo: </span><select class="form-control is-invalid" id="input-select" title="Dbo class" v-model="objectClass" required>
        <option v-for="dbo in availableDbos" :value="dbo">{{ dbo }}</option>
    </select><button v-on:click="ids">Get all IDs for Dbo (can be slow)</button><br/>
    <span>Id: </span><input class="form-control is-invalid" placeholder="id" id="input-id" v-model="objectId"/><br/>
    <button v-on:click="get">Get Data</button><br/>
    <span>{{ errorMessage }}</span>
</th>
<td width="75%">
    <pre>{{ objectData }}</pre>
</td>
</tr></table>
</div>
`,
        mounted: function() {
            var self = this;
            axios({
                method: 'get',
                url: '/api/admin/dbo/all'
            }).then(function (response) {
                if (response.data.success === true) {
                    console.log(response.data.data);
                    self.availableDbos = response.data.data;
                }
                else {
                    self.errorMessage = "FAIL: "+response;
                }
            }).catch(function(error) {
                self.errorMessage = "ERROR: "+error;
            });
        },
        watch: {
            objectClass: function() {
                document.getElementById("input-select").classList.remove("is-invalid");
                document.getElementById("input-select").classList.add("is-valid");
                console.log("objectClass changed: "+this.objectClass);
            },
            objectId: function() {
                if (this.objectId > 0) {
                    document.getElementById("input-id").classList.remove("is-invalid");
                    document.getElementById("input-id").classList.add("is-valid");
                }
                else {
                    document.getElementById("input-id").classList.remove("is-valid");
                    document.getElementById("input-id").classList.add("is-invalid");
                }
                console.log("objectId changed: "+this.objectId);
            }

        },
        methods: {
            get() {
                console.log("objectName="+this.objectClass+":"+this.objectId);
                if (this.objectClass !== null && this.objectId > 0) {
                    var self = this;
                    axios({
                        method: 'get',
                        url: '/api/admin/dbo/' + this.objectClass + '/' + this.objectId
                    }).then(function (response) {
                        if (response.data.success === true) {
                            console.log(response.data);
                            self.objectData = JSON.stringify(response.data, null, 2);
                        }
                        else {
                            console.log(response.data);
                            self.objectData = JSON.stringify(response.data, null, 2);
                        }
                    }).catch(function (error) {
                        self.objectData = error;
                    });
                }
                else {
                    console.log("Invalid: Dbo="+this.objectClass+" id="+this.objectId);
                }
            },
            ids() {
                console.log("objectName="+this.objectClass+":ids");
                if (this.objectClass !== null) {
                    var self = this;
                    axios({
                        method: 'get',
                        url: '/api/admin/dbo/' + this.objectClass + '/ids'
                    }).then(function (response) {
                        if (response.data.success === true) {
                            console.log(response.data);
                            self.objectData = JSON.stringify(response.data, null, 2);
                        }
                        else {
                            console.log(response.data);
                            self.objectData = JSON.stringify(response.data, null, 2);
                        }
                    }).catch(function (error) {
                        self.objectData = error;
                    });
                }
            }
        }
    });

    var homeApp = new Vue({
        el: '#admin-home-app',
        data: {
            app: <%=VueHelper.buildData().toString()%>
        }
    });

    console.log(homeApp);

</script>
</html>
