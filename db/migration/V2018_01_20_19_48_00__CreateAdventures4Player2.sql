INSERT INTO adventure(id,player__id) VALUES
  (2,2);

INSERT INTO encounter(id,adventure__id) VALUES
  (2,2)  -- More to do here
;
INSERT INTO enemy_card(id,encounter__id,name,level,xp,strength,agility,stickers) VALUES
  (3,2,'Goblin 1',8,100,30,70,'NOP')
  ,(4,2,'Goblin 2',6,80,65,65,'NOP')
  ,(5,2,'Goblin 2',6,80,65,65,'NOP')
  ,(6,2,'Goblin 2',6,80,65,65,'NOP')
  ,(7,2,'Goblin 2',6,80,65,65,'NOP')
  ,(8,2,'Goblin 2',6,80,65,65,'NOP')
;
