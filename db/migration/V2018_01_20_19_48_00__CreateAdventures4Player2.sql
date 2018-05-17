INSERT INTO adventure(id,player__id) VALUES
  (2,2);

INSERT INTO encounter(id,adventure__id) VALUES
  (2,2)  -- More to do here
;
INSERT INTO enemy_card(id,encounter__id,name,level,xp,strength,agility,stamina) VALUES
  (3,2,'Goblin 1',8,100,30,70,45)
  ,(4,2,'Goblin 2',6,80,65,65,40)
;

INSERT INTO enemy_card_sticker(id,enemy_card__id,name) VALUES
  (6,3,'Goblin 1 Sticker One')
  ,(7,4,'Goblin 2 Sticker 1')
  ,(8,4,'Goblin 2 Sticker 2')
;