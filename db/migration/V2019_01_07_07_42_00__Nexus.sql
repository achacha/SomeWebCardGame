--
-- Nexus is associated with a Player
--
CREATE TABLE nexus
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer, -- Player id of the owner of this nexus

  -- Level of the nexus
  level integer,

  -- Structures
  energy_gatherer_type integer,
  material_processing_type integer,

  --
  CONSTRAINT "FK_nexus_player__id" FOREIGN KEY (player__id)
    REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "UNIQUE_nexus__player" UNIQUE (player__id)

)
  WITH (
    OIDS=FALSE
  );
ALTER TABLE nexus OWNER TO sawcog;
GRANT ALL ON nexus TO sawcog;
COMMENT ON COLUMN nexus.player__id IS 'Player id of the owner of this nexus';
COMMENT ON COLUMN nexus.level IS 'Level of the nexus';
COMMENT ON COLUMN nexus.energy_gatherer_type IS 'Energy gatherer type';
COMMENT ON COLUMN nexus.material_processing_type IS 'Material processing type';
