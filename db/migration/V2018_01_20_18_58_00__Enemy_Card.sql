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
  stamina integer,
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


--
-- Enemy card sticker
--
CREATE TABLE enemy_card_sticker
(
  id serial NOT NULL PRIMARY KEY,
  enemy_card__id integer, -- Card which owns this
  name character varying(256) NOT NULL,
  --
  CONSTRAINT enemy_card_sticker_card__id_fkey FOREIGN KEY (enemy_card__id)
  REFERENCES enemy_card (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE enemy_card_sticker OWNER TO sawcog;
GRANT ALL ON enemy_card_sticker TO sawcog;
COMMENT ON COLUMN enemy_card_sticker.enemy_card__id IS 'Enemy Card id owner of this sticker';
