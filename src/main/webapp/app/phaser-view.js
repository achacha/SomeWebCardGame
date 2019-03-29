Vue.component('phaser-view', {
    template:
`<script>
        let game = new Phaser.Game(800, 600, Phaser.AUTO, '', { preload: preload, create: create, update: update });
        let cards = [];
        let cardtexts = [];
        function preload() {

            game.load.image('logo', 'images/phaser.png');
            game.load.image('cardback', 'images/quasit.png');

            game.load.json('login', 'api/auth/login');
            game.load.json('player', 'api/player');
        }

        function create() {
            // Central logo
            let textStyleWhite = { fill: '#ffffff', fontSize: '8pt' };

            let logo = game.add.sprite(game.world.centerX, game.world.centerY, 'logo');
            logo.anchor.setTo(0.5, 0.5);
            logo.inputEnabled = true;
            var text0 = game.add.text(0,0,'Planet text', textStyleWhite);
            logo.events.onInputDown.add(getInputDownFn("Clicked on planet", text0), this);

            let player = game.cache.getJSON('player').data[0];

            // Iterarate over cards and display them
            for (i=0; i<player.cards.length; ++i) {
                var c = player.cards[i];
                text0 = game.add.text(200*(i+1), 0, player.cards[i].name, textStyleWhite);
                var cardtext0 = game.add.text(0, 0, player.cards[i].name, { fill: '#ff00ff', fontSize: '8pt' });
                var card0 = game.add.sprite(10 + i*65, 50, 'cardback');
                card0.inputEnabled = true;
                card0.input.enableDrag();
                card0.inputEnabled = true;
                card0.events.onInputDown.add(getInputDownFn(c, "Clicked on card "+i, text0), this);
                cards.push(card0);
                cardtexts.push(cardtext0);
            }

            let login = game.cache.getJSON('login');
            if (login !== undefined) {
                game.add.text(0,10, login.user.fname, { fill: '#ffffff', fontSize: '8pt' });
            }
        }

        function getInputDownFn(card, basetext, text0) {
            return (function(sprite, pointer) {
                console.log("clicked "+text0.text+"  this="+this+"  card="+card);

                //loadAvailableAdventures()
                axios({
                    method: 'get',
                    url: '/api/adventure/available?playerId='+card.playerId
                }).then(function (response) {
                    if (response.data.success === true) {
                        //console.log(response.data.data);  //TODO: Continue
                        var availableAdventures = response.data.data;
                        for(i=0; i<availableAdventures.length; ++i)
                            console.log(i+": "+availableAdventures[i].title);
                    }
                    else {
                        console.log("FAIL: "+response);
                    }
                }).catch(function(error) {
                    console.log("ERROR: "+error);
                });


                if (!(text0.text !== basetext))
                    text0.text = basetext+", again.";
                else
                    text0.text = basetext;
            });
        }

        function update() {
            for (i=0; i<cards.length; ++i) {
                cardtexts[i].alignTo(cards[i], Phaser.BOTTOM_CENTER, 0, 4);

            }
        }
</script>`,
    data() {
        return {
        }
    },
    mounted() {
    }
});
