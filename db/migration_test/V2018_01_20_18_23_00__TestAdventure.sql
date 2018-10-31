INSERT INTO adventure(id,player__id) VALUES
  (1,1);

INSERT INTO encounter(id,adventure__id) VALUES
  (1,1)
;

INSERT INTO card(player__id,encounter__id,name,type,level,xp,strength,agility,stickers) VALUES
  (2,2,'Card 11','Goblin',8,100,30,70,'NOP')
  ,(2,2,'Card 12','Goblin',6,80,65,65,'NOP')
  ,(2,2,'Card 21','Elf',6,80,65,65,'NOP')
  ,(2,2,'Card 22','Elf',6,80,65,65,'NOP')
  ,(2,2,'Card 31','Human',6,80,65,65,'NOP')
  ,(2,2,'Card 32','Human',6,80,65,65,'NOP')
;
