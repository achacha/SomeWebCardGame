--
-- Cards associated with player
--
CREATE TABLE card
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer DEFAULT 0,
  active boolean NOT NULL DEFAULT true,
  encounter__id integer DEFAULT 0,
  name character varying(256) NOT NULL,
  type character varying(64) DEFAULT 'Grue',
  level integer,
  xp integer,
  strength integer default 10,
  agility integer default 10,
  damage integer default 10,
  stickers character varying(4096),
  --
  CONSTRAINT "FK_card_player__id" FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE card OWNER TO sawcog;
GRANT ALL ON card TO sawcog;
COMMENT ON COLUMN card.player__id IS 'Player id owner of this card';
COMMENT ON COLUMN card.active IS 'If card is active, inactive cards are not visible by default';
COMMENT ON COLUMN card.encounter__id IS 'If enemy card this will be non zero for encounter that owns it';
