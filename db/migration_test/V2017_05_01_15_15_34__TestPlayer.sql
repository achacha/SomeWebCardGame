INSERT INTO public.player(id,login__id,energy) VALUES
  (1,2,1200);

INSERT INTO public.inventory(id,player__id) VALUES
  (1,1);

INSERT INTO public.item(inventory__id,type,quantity) VALUES
   (1,1,31)   --white
  ,(1,2,16)  --green
  ,(1,3,9)  --blue
  ,(1,4,5)  --purple
  ,(1,5,2)  --orange
  ,(1,6,1)  --red
  ;
