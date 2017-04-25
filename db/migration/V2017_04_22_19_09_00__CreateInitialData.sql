-- By default Global will load properties with NULL, then overlay the application specific ones
-- TODO: CLEANUP
INSERT into public.global_properties(name,value) VALUES
  ('uri.home','/'),
  ('uri.login.home','/login.jsp'),
  ('uri.login.target','/api/auth/login'),
  ('url.home.public','http://localhost:10080'),
  ('uri.admin.login','/admin/login.jsp')
;

-- su (password should never be in code and must be changed in production after initial deploy)
INSERT into public.login(id,email,fname,lname,pwd,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (1, 'su', 'Super', 'User', '54320e79a53f95067e875a5fc3359f0139496346d96f78afd6c1c28933fd0e78', 'CST', 255, true, '127 Double Zero Way','Suite 1','Austin','TX','78701','512-000-0000','650-000-0000');
