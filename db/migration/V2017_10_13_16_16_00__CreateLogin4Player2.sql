--// Sample login #4
INSERT into login(id,email,fname,lname,pwd,salt,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (10, 'alex', 'Alex', '#4', 'a561edc0e8673e6aca62241cedc0012dd473c4a507ade8ef060836b7b38674fd', 'k1OHNXqwThRHGpkKaTLM4YHUok5r1FQW51xVCibtHBQZrgzTOGxgts9oTA620P7C',
    'CST', 100, false, '1 Home Address', null, 'Austin', 'TX', '78731', '512-000-0000', '650-000-0000');

--// Player #2 associated with login #4
INSERT INTO player(id,login__id,energy) VALUES
  (2,10,5000);

--// Invetory #2 for player #2
INSERT INTO inventory(id,player__id) VALUES
  (2,2);

INSERT INTO item(inventory__id,type,quantity) VALUES
   (2,1,200) --white
  ,(2,2,100) --green
  ,(2,3,50)  --blue
  ,(2,4,25)  --purple
  ,(2,5,12)  --orange
  ,(2,6,6)   --red
;

-- Card data
INSERT INTO card(player__id,name,type,level,xp,strength,agility,damage,stickers) VALUES
   (2,'Patient Zero','Human',6,18000,8,18,12,'HOT_MAJOR')
  ,(2,'Ol One Eye','Goblin',3,48000,14,12,10,'HOT_MINOR')
  ,(2,'Sylvae','Elf',4,99900,12,15,11,'NOP')
;