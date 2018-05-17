-- -----------------------------------------------------
-- login an individual
--
CREATE TABLE login
(
  -- Login data
  id serial NOT NULL PRIMARY KEY,
  email character varying(256) DEFAULT NULL,
  pwd character varying(100) NOT NULL,
  created_on timestamp without time zone NOT NULL DEFAULT now(),
  last_login_on timestamp without time zone NOT NULL DEFAULT now(),
  security_level integer DEFAULT 0,                       -- Security level, defaults to 0 which allows public access only
  is_superuser boolean NOT NULL DEFAULT false,            -- Special flag to allow full access and admin rights
  is_active boolean NOT NULL DEFAULT true,                -- Inactive users cannot login or access the system
  locale character varying(32) NOT NULL DEFAULT 'en_US',  -- language_country_variant  each 2 character code  country and variant optional
  timezone character varying(64) NOT NULL DEFAULT 'UTC',  -- java.time.ZoneId and java.util.TimeZone
  -- Persona data
  fname character varying(100) DEFAULT NULL,
  lname character varying(200) DEFAULT NULL,
  address1 character varying(1024) DEFAULT NULL,
  address2 character varying(1024) DEFAULT NULL,
  city character varying(100) DEFAULT NULL,
  state character varying(50) DEFAULT NULL,
  postal character varying(30) DEFAULT NULL,
  country character varying(50) DEFAULT NULL,
  phone1 character varying(50) DEFAULT NULL,
  phone2 character varying(50) DEFAULT NULL,
  --
  CONSTRAINT "UNIQUE_login__email" UNIQUE (email)
)
WITH (
OIDS=FALSE
);
ALTER TABLE login OWNER TO sawcog;
GRANT ALL ON login TO sawcog;

-- Attributes name/value pairs per login
CREATE TABLE login_attr
(
  id serial NOT NULL PRIMARY KEY,
  name character varying(256) NOT NULL,
  value character varying(4096),
  login_id integer NOT NULL,
  --
  CONSTRAINT "FK_login_attr__login__login_id" FOREIGN KEY (login_id) REFERENCES login (id) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "UNIQUE_login_attr__login_id__name" UNIQUE (login_id, name)
)
WITH (
OIDS = FALSE
)
;
ALTER TABLE login_attr OWNER TO sawcog;
GRANT ALL ON login_attr TO sawcog;
COMMENT ON COLUMN login_attr.name IS 'Attribute name';
COMMENT ON COLUMN login_attr.value IS 'Value of the attribute';
COMMENT ON TABLE login_attr
IS 'Name:Value pairs associated with login by id';

-- -----------------------------------------------------
-- Internal event used for logging and auditing
--
CREATE TABLE event_log
(
  id serial NOT NULL PRIMARY KEY,
  created_on timestamp without time zone NOT NULL DEFAULT now(),
  login_id integer,  -- NULL login implies internal server generated event
  event_id integer NOT NULL,
  data json
)
WITH (
OIDS=FALSE
);
ALTER TABLE event_log OWNER TO sawcog;
GRANT ALL ON event_log TO sawcog;

-- -----------------------------------------------------
-- Global name/value pairs
--
CREATE TABLE global_properties
(
  id serial NOT NULL PRIMARY KEY,
  created_on timestamp without time zone NOT NULL DEFAULT now(),
  name character varying(256) NOT NULL,
  value character varying(4096),
  --
  CONSTRAINT "UNIQUE_global_properties__name" UNIQUE (name)
)
WITH (
OIDS=FALSE
);
ALTER TABLE global_properties OWNER TO sawcog;
GRANT ALL ON global_properties TO sawcog;
