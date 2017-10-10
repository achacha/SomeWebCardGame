--
-- Cards associated with player
--
CREATE TABLE card
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer, -- Player id of the owner of this item
  name character varying(256) NOT NULL,
  --
  CONSTRAINT card_player__id_fkey FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE item OWNER TO sawcog;
COMMENT ON COLUMN card.player__id IS 'Player id of the owner of this inventory';

