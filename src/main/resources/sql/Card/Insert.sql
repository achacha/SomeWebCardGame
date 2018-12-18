insert into public.card(id,player__id,encounter__id,name,type,level,xp,strength,agility,stickers)
 values(DEFAULT,?,?,?,?,?,?,?,?,?) returning id;