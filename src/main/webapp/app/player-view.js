Vue.component('player-view', {
    template:
`<div v:if="loaded" class="accordion" id="accordionPlayer">
    <div v-for="player in players" class="card">
        <div class="player-header" v-bind:id="'heading-'+player.id">
            <h2 class="mb-0">
                <button class="btn btn-link" type="button" data-toggle="collapse" v-bind:data-target="'#collapse-'+player.id" aria-expanded="true" 
                        v-bind:aria-controls="'collapse-'+player.id">
                    {{ player.name }}
                </button>
            </h2>
        </div>

        <div v-bind:id="'collapse-'+player.id" class="collapse" v-bind:aria-labelledby="'heading-'+player.id" data-parent="#accordionPlayer">
            <div class="player-body">
                <ul>
                    <li>Name: {{ player.name }}</li>
                    <li>Materials: {{ player.inventory.materials }}</li>
                    <li>Materials: {{ player.inventory.materials }}</li>
                    <li>Resources: {{ player.inventory.resources }}</li>
                </ul>
                <div v-for="card in player.cards" style="margin-left: 24px;">
                    <div class="accordion" id="accordionCard">
                        <div class="card-header" v-bind:id="'heading-card-'+card.id">
                            <h2 class="mb-0">
                                <button class="btn btn-link" type="button" data-toggle="collapse" v-bind:data-target="'#collapse-card-'+card.id" aria-expanded="true"
                                        v-bind:aria-controls="'collapse-card-'+card.id">
                                    {{ card.name }}
                                </button>
                            </h2>
                        </div>
                    </div>

                    <div v-bind:id="'collapse-card-'+card.id" class="collapse" v-bind:aria-labelledby="'heading-card-'+card.id" data-parent="#accordionCard">
                        <div class="card-body">
                            <ul>
                                <li>{{ card.id }}: {{ card.name }}</li>
                                <li>{{ card.type }}:{{ card.level }}</li>
                                <li>STR:{{ card.strength }} AGI:{{ card.agility }} DMG:{{ card.damage }}</li>
                                <li><span v-if="card.stickers.length" v-for="sticker in card.stickers" style="background-color: darkkhaki">{{sticker}}&nbsp;&nbsp;</span></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
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
