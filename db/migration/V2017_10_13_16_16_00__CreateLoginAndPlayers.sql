--// Sample login #4
INSERT into login(id,email,fname,lname,pwd,salt,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (10, 'alex', 'Alex', '#4', 'a561edc0e8673e6aca62241cedc0012dd473c4a507ade8ef060836b7b38674fd', 'k1OHNXqwThRHGpkKaTLM4YHUok5r1FQW51xVCibtHBQZrgzTOGxgts9oTA620P7C',
    'CST', 100, false, '1 Home Address', null, 'Austin', 'TX', '78731', '512-000-0000', '650-000-0000');

--// Player #2 associated with login #4
INSERT INTO player(id,login__id,name) VALUES
  (10,10,'Qlex'),
  (11,10,'Skoll');

--// Inventory #2 for player #2
INSERT INTO inventory(id,player__id,energy,materials) VALUES
  (10,10,5000,10000),
  (11,11,6000,7000);

-- Card data
INSERT INTO card(player__id,name,type,level,xp,strength,agility,damage,stickers) VALUES
  (10,'Patient Zero','Human',6,18000,8,18,12,'HOT_AT5,DOT_AT3'),
  (10,'Ol One Eye','Goblin',3,48000,14,12,10,'HOT_AT3'),
  (10,'Sylvae','Elf',4,99900,12,15,11,'DOT_AT5'),

  (11,'Goblin 1','Goblin',1,0,8,12,12,'HOT_AT1'),
  (11,'Goblin 2','Goblin',1,0,9,11,12,'HOT_AT1'),
  (11,'Goblin 3','Goblin',1,0,10,10,12,'HOT_AT1'),
  (11,'Sweaty Mo','Human',4,1200,15,10,9,'HOT_AT5,DOT_AT1'),
  (11,'Laralale','Elf',6,99900,10,18,16,'HOT_AT1,DOT_AT5'),
  (11,'Coralale','Elf',6,91900,11,16,18,'HOT_AT3,DOT_AT1')
;