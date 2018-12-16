insert into public.card(id,player__id,name,type,xp,strength,agility,stickers)
 values(DEFAULT,?,?,?,?,?,?,?) returning id;