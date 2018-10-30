--
-- Cards associated with player
--
CREATE TABLE card
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer,
  name character varying(256) NOT NULL,
  level integer,
  xp integer,
  strength integer,
  agility integer,
  stickers character varying(4096),
  --
  CONSTRAINT card_player__id_fkey FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE card OWNER TO sawcog;
GRANT ALL ON card TO sawcog;
COMMENT ON COLUMN card.player__id IS 'Player id owner of this card';
