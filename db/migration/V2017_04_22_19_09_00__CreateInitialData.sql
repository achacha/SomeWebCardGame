-- By default Global will load properties with NULL, then overlay the application specific ones
INSERT into global_properties(name,value) VALUES
  ('uri.home','/'),
  ('uri.login.home','/login.jsp'),
  ('uri.login.target','/api/auth/login'),
  ('url.home.public','http://localhost:10080')
;

-- su (password should never be in code and must be changed in production after initial deploy)
INSERT into login(id,email,fname,lname,pwd,salt,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (1, 'su', 'Super', 'User', 'ac63dcc04031c50e621498fafb841957fd786af9347e6ad2075b0744963dda4f', 'KjQl2AtobXAKDb78xzuBW2rLbTXnxUbLTn05xEymub4fqcYHR7ezi6ynjuBjKlax', 'CST', 255, true, '127 Double Zero Way','Suite 1','Austin','TX','78701','512-000-0000','650-000-0000');
