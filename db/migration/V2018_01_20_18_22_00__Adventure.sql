
--
-- Adventure contains list of Encounter
-- Adventure is associated with a Player
--
CREATE TABLE adventure
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer, -- Player id of the owner of this item
  --
  CONSTRAINT adventure_player__id_fkey FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE adventure OWNER TO sawcog;
GRANT ALL ON adventure TO sawcog;
COMMENT ON COLUMN adventure.player__id IS 'Player id of the owner of this adventure';


--
-- Encounter
--
CREATE TABLE encounter
(
  id serial NOT NULL PRIMARY KEY,
  adventure__id integer, -- Adventure which owns this
  --
  CONSTRAINT encounter_adventure__id_fkey FOREIGN KEY (adventure__id)
  REFERENCES adventure (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE encounter OWNER TO sawcog;
GRANT ALL ON encounter TO sawcog;
COMMENT ON COLUMN encounter.adventure__id IS 'Adventure id owner of this item';
