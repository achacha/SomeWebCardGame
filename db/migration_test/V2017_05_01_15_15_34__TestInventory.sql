INSERT INTO public.player(id,login__id) VALUES
  (1,2);

INSERT INTO public.inventory(id,player__id,energy) VALUES
  (1,1,1200);

INSERT INTO public.item(id,inventory__id,type,quantity) VALUES
   (1,1,1,31)   --white
  ,(2,1,2,16)  --green
  ,(3,1,3,9)  --blue
  ,(4,1,4,5)  --purple
  ,(5,1,5,2)  --orange
  ,(6,1,6,1)  --red
  ;

INSERT INTO public.card(id,player__id,name) VALUES
  (1,1,'Patient Zero')
 ,(2,1,'One Eye')
  ;
