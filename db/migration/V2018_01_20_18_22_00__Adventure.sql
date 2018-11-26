
--
-- Adventure contains list of Encounter
-- Adventure is associated with a Player
--
CREATE TABLE adventure
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer NOT NULL UNIQUE,    -- Player id of the owner of this item, only 1 adventure per player id
  created time NOT NULL default now(),
  --
  CONSTRAINT adventure_player__id_fkey FOREIGN KEY (player__id) REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE adventure OWNER TO sawcog;
GRANT ALL ON adventure TO sawcog;
COMMENT ON COLUMN adventure.player__id IS 'Player id of the owner of this adventure';

--
-- Adventure archive
-- After adventure finishes it is moved from adventure to adventure_complete
--
CREATE TABLE adventure_archive
(
  id serial NOT NULL PRIMARY KEY,
  original_id integer,     -- Original id
  original_created time,   -- Original created
  player__id integer NOT NULL,      -- Player id of the owner of this item
  completed time NOT NULL default now(),
  --
  CONSTRAINT adventure_player__id_fkey FOREIGN KEY (player__id) REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE adventure_archive OWNER TO sawcog;
GRANT ALL ON adventure_archive TO sawcog;
COMMENT ON COLUMN adventure_archive.player__id IS 'Player id of the owner of this adventure';

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
  CONSTRAINT encounter_adventure__id_fkey FOREIGN KEY (adventure__id) REFERENCES adventure (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
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
  CONSTRAINT encounter_adventure__id_fkey FOREIGN KEY (adventure_archive__id) REFERENCES adventure_archive (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE encounter_archive OWNER TO sawcog;
GRANT ALL ON encounter_archive TO sawcog;
