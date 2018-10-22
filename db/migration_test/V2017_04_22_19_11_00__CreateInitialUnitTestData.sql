-- // Many tests depend on this data, change with caution
-- // This script should not run in production

-- junit:test, junitadmin:test
INSERT into login(id,email,fname,lname,pwd,salt,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (2, 'junit', 'JUnit', 'User', '6f65a83a228e35594cd8485fe5e83052425ac591e0f96258a97b2db00ffb6a55', 'UnitTest',
    'UDT', 100, false, 'Area 51',NULL,'Amargosa Valley','NV','89020','000-000-0000',NULL),
  (3, 'junitadmin', 'JUnitAdmin', 'Admin', '6f65a83a228e35594cd8485fe5e83052425ac591e0f96258a97b2db00ffb6a55', 'UnitTest',
    'UDT', 255, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
  (4, 'junitsu', 'JUnitSuperuser', 'Super', '6f65a83a228e35594cd8485fe5e83052425ac591e0f96258a97b2db00ffb6a55', 'UnitTest',
    'UDT', 255, true, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


-- Login attributes
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'color', 'red');
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'shape', 'round');
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'size', 'large');
