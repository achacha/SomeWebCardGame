
--
-- Adventure contains list of Encounter
-- Adventure is associated with a Player
--
CREATE TABLE adventure
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer NOT NULL UNIQUE,    -- Player id of the owner of this item, only 1 adventure per player id
  player_cards integer[] NOT NULL,
  title varchar(256) NOT NULL,
  created timestamp NOT NULL default now(),
  --
  CONSTRAINT "FK_adventure_player__id" FOREIGN KEY (player__id) REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE adventure OWNER TO sawcog;
GRANT ALL ON adventure TO sawcog;
COMMENT ON COLUMN adventure.player__id IS 'Player id of the owner of this adventure';
COMMENT ON COLUMN adventure.player_cards IS 'Array of ordered card ids that player is sending on this adventure';
COMMENT ON COLUMN adventure.title IS 'Generated title for this adventure';
COMMENT ON COLUMN adventure.created IS 'Timestamp set when adventure is made active';

--
-- Adventure archive
-- After adventure finishes it is moved from adventure to adventure_complete
--
CREATE TABLE adventure_archive
(
  id serial NOT NULL PRIMARY KEY,
  original_id integer,          -- Original id
  original_created timestamp,   -- Original created
  player__id integer NOT NULL,  -- Player id of the owner of this item
  player_cards integer[] NOT NULL,
  title varchar(256) NOT NULL,  -- Original title
  completed timestamp NOT NULL default now(),
  --
  CONSTRAINT "FK_adventure_player__id" FOREIGN KEY (player__id) REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE adventure_archive OWNER TO sawcog;
GRANT ALL ON adventure_archive TO sawcog;
COMMENT ON COLUMN adventure_archive.player__id IS 'Player id of the owner of this adventure';
COMMENT ON COLUMN adventure.player_cards IS 'Array of ordered card ids that player sent on this adventure';
COMMENT ON COLUMN adventure_archive.title IS 'Generated title for this adventure';
COMMENT ON COLUMN adventure_archive.original_id IS 'Original id of this adventure';
COMMENT ON COLUMN adventure_archive.original_created IS 'Original create time';

--
-- Encounter
-- Encounter is associated with adventure
--
CREATE TABLE encounter
(
  id serial NOT NULL PRIMARY KEY,
  adventure__id integer NOT NULL, -- Adventure which owns this
  enemy_cards json,
  result integer,
  --
  CONSTRAINT "FK_encounter_adventure__id" FOREIGN KEY (adventure__id) REFERENCES adventure (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE encounter OWNER TO sawcog;
GRANT ALL ON encounter TO sawcog;
COMMENT ON COLUMN encounter.adventure__id IS 'Adventure id owner of this item';

--
-- Encounter archive
-- After adventure finishes all associated encounters are moved to encounter_complete
--
CREATE TABLE encounter_archive
(
  id serial NOT NULL PRIMARY KEY,
  adventure_archive__id integer NOT NULL,
  enemy_cards json,
  result integer,
  --
  CONSTRAINT "FK_encounter_adventure__id" FOREIGN KEY (adventure_archive__id) REFERENCES adventure_archive (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE encounter_archive OWNER TO sawcog;
GRANT ALL ON encounter_archive TO sawcog;
