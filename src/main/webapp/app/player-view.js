Vue.component('player-view', {
    template:
`<div v:if="loaded" class="container-fluid">
    <div class="row">
        <button v-for="player in players">{{ player.name }}</button>
    </div>
    <div v-for="player in players" class="row">
        <ul>
            <li>Name: {{ player.name }}</li>
            <li>Materials: {{ player.inventory.materials }}</li>
            <li>Materials: {{ player.inventory.materials }}</li>
            <li>Resources: {{ player.inventory.resources }}</li>
            <li v-for="card in player.cards" style="background-color: darkseagreen; border: 1px">
                <ul>
                    <li>{{ card.id }}: {{ card.name }}</li><br/>
                    <li>{{ card.type }}:{{ card.level }}</li>
                    <li>STR:{{ card.strength }} AGI:{{ card.agility }} DMG:{{ card.damage }}</li>
                    <li><span v-if="card.stickers.length" v-for="sticker in card.stickers" style="background-color: darkkhaki">{{sticker}} </span></li>
                </ul>
            </li>
        </ul>
    </div>
</div>`,
    data() {
        return {
            players: [],
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
                    self.players = response.data.data;
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
