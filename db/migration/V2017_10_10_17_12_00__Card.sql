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
  stamina integer,
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


--
-- Card sticker
--
CREATE TABLE card_sticker
(
  id serial NOT NULL PRIMARY KEY,
  card__id integer, -- Card which owns this
  name character varying(256) NOT NULL,
  --
  CONSTRAINT card_sticker_card__id_fkey FOREIGN KEY (card__id)
  REFERENCES card (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE card_sticker OWNER TO sawcog;
GRANT ALL ON card_sticker TO sawcog;
COMMENT ON COLUMN card_sticker.card__id IS 'Card id owner of this sticker';
