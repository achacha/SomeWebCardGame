insert into public.item(id,inventory__id,type,quantity) values(DEFAULT,?,?,?) returning id;