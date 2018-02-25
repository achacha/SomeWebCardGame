<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>hello phaser!</title>
  <script src="./js/phaser-ce/build/phaser.js"></script>
</head>
<body>
<%
    if (CallContextTls.get().getLogin() == null) {
        session.setAttribute(CallContext.SESSION_REDIRECT_FROM, "/");
        response.sendRedirect("/login.jsp");
        return;
    }
%>

<script type="text/javascript">

    window.onload = function() {

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
                card0.events.onInputDown.add(getInputDownFn("Clicked on card "+i, text0), this);
                cards.push(card0);
                cardtexts.push(cardtext0);
            }

            let login = game.cache.getJSON('login');
            if (login !== undefined) {
                game.add.text(0,10, login.user.fname, { fill: '#ffffff', fontSize: '8pt' });
            }
        }

        function getInputDownFn(basetext, text0) {
            return (function(sprite, pointer) {
                console.log("clicked "+text0.text+"  this="+this);
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
    };
</script>

</body>
</html>