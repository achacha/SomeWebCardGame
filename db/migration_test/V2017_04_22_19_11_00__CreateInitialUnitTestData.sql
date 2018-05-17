-- // Many tests depend on this data, change with caution
-- // This script should not run in production

-- junit:test, iunitadmin:test
INSERT into login(id,email,fname,lname,pwd,timezone,security_level,is_superuser,address1,address2,city,state,postal,phone1,phone2) VALUES
  (2, 'junit', 'JUnit', 'User', '2afc30ea5c2aca9fdc2658a0579efc1c10839fe65fe926d545b75fdf4502a252', 'UDT', 100, false, 'Area 51',NULL,'Amargosa Valley','NV','89020','000-000-0000',NULL),
  (3, 'junitadmin', 'JUnitAdmin', 'Admin', 'b486ca686b67a645d8d63e2cd45dca41784920cb7656cb8bb36b34ea5e70d8a9', 'UDT', 255, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


-- Login attributes
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'color', 'red');
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'shape', 'round');
INSERT INTO login_attr(login_id, name, value) VALUES(2, 'size', 'large');
