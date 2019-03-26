<template>
    <div class="LoginView.vue">
        <pre v-text="$props"></pre>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
        props: {
        },
        data: async function() {
        let responseData = {
            success: false,
            state: "Unfinished"
        };

        var promiseData = await axios.get('/api/auth/login')
            .then(function (response) {
                if (response.data.success === true) {
                    //console.log("SUCCESS: "+JSON.stringify(response.data.user));
                    responseData['data'] = response.data.user;
                    responseData['state'] = 'Complete';
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
        return responseData;
    }

    };
</script>
