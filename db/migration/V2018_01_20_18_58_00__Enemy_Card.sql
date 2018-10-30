--
-- Enemy cards associated with encounter
--
CREATE TABLE enemy_card
(
  id serial NOT NULL PRIMARY KEY,
  encounter__id integer,
  name character varying(256) NOT NULL,
  level integer,
  xp integer,
  strength integer,
  agility integer,
  stickers character varying(4096),
  --
  CONSTRAINT card_encounter__id_fkey FOREIGN KEY (encounter__id)
  REFERENCES encounter (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE enemy_card OWNER TO sawcog;
GRANT ALL ON enemy_card TO sawcog;
COMMENT ON COLUMN enemy_card.encounter__id IS 'Encounter id owner of this enemy card';
