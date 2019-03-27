Vue.component('login-view', {
    template: `
<div v:if="loaded">
    <span>Name: {{ user.fname }} ({{ user.email }})</span><br/>
    <div v-if="!success">
        <span>State: {{ state }}</span><br/>
    </div>
    <div v-else>
        <span>State: Ok</span><br/>
    </div>
</div>`,
    data() {
        return {
            user: {},
            loaded: false,
            success: true,
            state: "Incomplete"
        }
    },
    mounted() {
        var self = this;
        axios
            .get('/api/auth/login')
            .then(function (response) {
                if (response.data.success === true) {
                    self.user = response.data.user;
                    self.success = true;
                    console.log("SUCCESS: " + JSON.stringify(response.data.user));
                } else {
                    console.log("FAIL: " + response);
                    self.state = "FAIL: "+response;
                }
            })
            .catch(function (error) {
                console.log("ERROR: " + error);
                self.state = "ERROR:" + error;
            })
            .finally(() => {
                self.loaded = true;
                self.state = "Complete";
            });
    }
});
