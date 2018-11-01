INSERT INTO adventure(id,player__id) VALUES
  (2,2);

INSERT INTO encounter(id,adventure__id) VALUES
  (2,2)  -- More to do here
;
INSERT INTO card(player__id,encounter__id,name,type,level,xp,strength,agility,damage, stickers) VALUES
  (2,2,'Goblin 1','Goblin',8,100,10,10,9,'NOP')
  ,(2,2,'Goblin 2','Goblin',6,80,11,10,9,'NOP')
  ,(2,2,'Elf 1','Elf',6,80,9,12,10,'NOP')
  ,(2,2,'ELf 2','Elf',6,80,9,11,10,'NOP')
  ,(2,2,'Human 1','Human',6,80,10,10,11,'NOP')
  ,(2,2,'Human 2','Human',6,80,11,9,11,'NOP')
;
