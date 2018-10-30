INSERT INTO adventure(id,player__id) VALUES
  (2,2);

INSERT INTO encounter(id,adventure__id) VALUES
  (2,2)  -- More to do here
;
INSERT INTO card(player__id,encounter__id,name,type,level,xp,strength,agility,stickers) VALUES
  (2,2,'Goblin 1','Goblin',8,100,30,70,'NOP')
  ,(2,2,'Goblin 2','Goblin',6,80,65,65,'NOP')
  ,(2,2,'Elf 1','Elf',6,80,65,65,'NOP')
  ,(2,2,'ELf 2','Elf',6,80,65,65,'NOP')
  ,(2,2,'Human 1','Human',6,80,65,65,'NOP')
  ,(2,2,'Human 2','Human',6,80,65,65,'NOP')
;
