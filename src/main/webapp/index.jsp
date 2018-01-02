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

        var game = new Phaser.Game(800, 600, Phaser.AUTO, '', { preload: preload, create: create, update: update });
        var text0, text1, text2;
        var card0, card1;
        var cardtext0, cardtext1;
        function preload() {

            game.load.image('logo', 'images/phaser.png');
            game.load.image('cardback', 'images/quasit.png');

            game.load.json('login', 'api/auth/login');
            game.load.json('player', 'api/player');
        }

        function create() {

            var logo = game.add.sprite(game.world.centerX, game.world.centerY, 'logo');
            logo.anchor.setTo(0.5, 0.5);
            logo.inputEnabled = true;
            logo.events.onInputDown.add(logo_listener, this);

            var textStyleWhite = { fill: '#ffffff', fontSize: '8pt' };
            text0 = game.add.text(0,0,'Planet text', textStyleWhite);
            text1 = game.add.text(250,0,'Card 1 Text', textStyleWhite);
            text2 = game.add.text(500,0,'Card 2 Text', textStyleWhite);

            var player = game.cache.getJSON('player').data[0];

            cardtext0 = game.add.text(0, 0, player.cards[0].name, { fill: '#ff00ff', fontSize: '8pt' });
            card0 = game.add.sprite(10,50, 'cardback');
            card0.inputEnabled = true;
            card0.input.enableDrag();
            card0.inputEnabled = true;
            card0.events.onInputDown.add(card1_listener, this);


            cardtext1 = game.add.text(0, 0, player.cards[1].name, { fill: '#ff00ff', fontSize: '8pt' });
            card1 = game.add.sprite(75,50, 'cardback');
            card1.inputEnabled = true;
            card1.input.enableDrag();
            card1.inputEnabled = true;
            card1.events.onInputDown.add(card2_listener, this);

            var login = game.cache.getJSON('login');
            if (login !== undefined) {
                game.add.text(0,10, login.user.fname, { fill: '#ffffff', fontSize: '8pt' });
            }
        }

        function logo_listener() {
            if (!(text0.text !== "Clicked on planet"))
                text0.text = "Clicked on planet, again.";
            else
                text0.text = "Clicked on planet";
        }

        function card1_listener() {
            if (!(text1.text !== "Clicked on card 1"))
                text1.text = "Clicked on card 1, again.";
            else
                text1.text = "Clicked on card 1";
        }

        function card2_listener() {
            if (!(text2.text !== "Clicked on card 2"))
                text2.text = "Clicked on card 2, again.";
            else
                text2.text = "Clicked on card 2";
        }

        function update() {
            cardtext0.alignTo(card0, Phaser.BOTTOM_CENTER, 0, 4);
            cardtext1.alignTo(card1, Phaser.BOTTOM_CENTER, 0, 4);
        }
    };
</script>

</body>
</html>