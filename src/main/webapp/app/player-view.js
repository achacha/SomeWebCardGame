Vue.component('player-view', {
    template:
`<div v:if="loaded">
    <div v-for="datum in data">
        <span>Name: {{ datum.name }}</span><br/>
        <span>Materials: {{ datum.inventory.materials }}</span><br/>
        <span>Materials: {{ datum.inventory.materials }}</span><br/>
        <span>Resources: {{ datum.inventory.resources }}</span><br/>
        <div v-for="card in datum.cards">
            <span>{{ card.id }}: {{ card.name }}</span><br/>
            <span>&nbsp;&nbsp;&nbsp;&nbsp; {{ card.type }}:{{ card.level }}</span>
        </div>
    </div>
</div>`,
    data() {
        return {
            data: [],
            loaded: false,
            state: "Incomplete"
        }
    },
    mounted() {
        var self = this;
        axios
            .get('/api/player')
            .then(function (response) {
                if (response.data.success === true) {
                    self.data = response.data.data;
                    self.state = 'Success';
                    console.log("SUCCESS: " + JSON.stringify(response.data.data));
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
