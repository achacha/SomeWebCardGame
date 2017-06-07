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
        var text;
        function preload() {

            game.load.image('logo', 'images/phaser.png');
            game.load.image('quasit', 'images/quasit.png');

        }

        function create() {

            var logo = game.add.sprite(game.world.centerX, game.world.centerY, 'logo');
            logo.anchor.setTo(0.5, 0.5);

            text = game.add.text(0,0,'Something',  { fill: '#ffffff' });

            var quasit1 = game.add.sprite(10,50, 'quasit');
            quasit1.inputEnabled = true;
            quasit1.input.enableDrag();
            var quasit2 = game.add.sprite(75,50, 'quasit');
            quasit2.inputEnabled = true;
            quasit2.input.enableDrag();

            logo.inputEnabled = true;
            logo.events.onInputDown.add(listener, this);

        }

        function listener() {
            if (text.text == "Clicked")
                text.text = "Clicked again."
            else
                text.text = "Clicked";
        }

    };

</script>

</body>
</html>