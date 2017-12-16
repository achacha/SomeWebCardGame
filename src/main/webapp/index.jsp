<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>hello phaser!</title>
  <script src="./js/phaser/build/phaser.js"></script>
</head>
<body>

<script type="text/javascript">

    window.onload = function() {

        var game = new Phaser.Game(800, 600, Phaser.AUTO, '', { preload: preload, create: create });
        var text0, text1, text2;
        function preload() {

            game.load.image('logo', 'images/phaser.png');
            game.load.image('quasit', 'images/quasit.png');

        }

        function create() {

            var logo = game.add.sprite(game.world.centerX, game.world.centerY, 'logo');
            logo.anchor.setTo(0.5, 0.5);
            logo.inputEnabled = true;
            logo.events.onInputDown.add(logo_listener, this);

            text0 = game.add.text(0,0,'Planet text',  { fill: '#ffffff', fontSize: '8pt' });
            text1 = game.add.text(250,0,'Card 1 Text',  { fill: '#ffffff', fontSize: '8pt' });
            text2 = game.add.text(500,0,'Card 2 Text',  { fill: '#ffffff', fontSize: '8pt' });

            var quasit1 = game.add.sprite(10,50, 'quasit');
            quasit1.inputEnabled = true;
            quasit1.input.enableDrag();
            quasit1.inputEnabled = true;
            quasit1.events.onInputDown.add(card1_listener, this);


            var quasit2 = game.add.sprite(75,50, 'quasit');
            quasit2.inputEnabled = true;
            quasit2.input.enableDrag();
            quasit2.inputEnabled = true;
            quasit2.events.onInputDown.add(card2_listener, this);
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
    };

</script>

</body>
</html>