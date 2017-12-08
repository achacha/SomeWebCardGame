--// Sample login #4
INSERT into public.login(id,email,fname,lname,pwd,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (4, 'alex', 'Alex', '#4', 'd773c62aedf24f82b411b8268d37de73a9c8eed31be2cc1d6011bb1d823d8fef', 'CST', 100, false, '1 Home Address', null, 'Austin', 'TX', '78731', '512-000-0000', '650-000-0000');

--// Player #2 associated with login #4
INSERT INTO public.player(id,login__id,energy) VALUES
  (2,4,5000);

--// Invetory #2 for player #2
INSERT INTO public.inventory(id,player__id) VALUES
  (2,2);

INSERT INTO public.item(inventory__id,type,quantity) VALUES
   (2,1,200) --white
  ,(2,2,100) --green
  ,(2,3,50)  --blue
  ,(2,4,25)  --purple
  ,(2,5,12)  --orange
  ,(2,6,6)   --red
;
www
-- Card data
INSERT INTO public.card(id,player__id,name) VALUES
   (4, 2,'Patient Zero')
  ,(5, 2,'Ol One Eye')
  ,(6, 2,'Card Two')
;

INSERT INTO public.card_sticker(id,card__id,name) VALUES
   (6,4,'Infection')
  ,(7,4,'Contagious')
  ,(8,4,'Festering Laceration')
  ,(9,5,'Patch')
  ,(10,5,'Infrared Enhancement')
;