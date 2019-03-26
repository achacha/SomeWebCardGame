Vue.component('login-view', {
    template: `
<div v:if="loaded">
    <span>Name: {{ user.fname }}</span><br/>
    <span>Email: {{ user.email }}</span><br/>
    <span>State: {{ state }}</span><br/>
</div>`,
    data() {
        return {
            user: {},
            loaded: false,
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
                    self.state = 'Success';
                    console.log("SUCCESS: " + JSON.stringify(response.data.user));
                } else {
                    console.log("FAIL: " + response);
                    self.state = response;
                }
            })
            .catch(function (error) {
                console.log("ERROR: " + error);
                self.state = error;
            })
            .finally(() => self.loaded = true);
    }
});
