
--
-- Player associated with Login
--
CREATE TABLE player
(
  id serial NOT NULL PRIMARY KEY,
  login__id integer, -- Login id of the owner of this item
  name varchar(128),
  last_tick timestamp NOT NULL DEFAULT now(),
  --
  CONSTRAINT player_login__id_fkey FOREIGN KEY (login__id)
  REFERENCES login (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE player OWNER TO sawcog;
GRANT ALL ON player TO sawcog;
COMMENT ON COLUMN player.login__id IS 'Login id of the owner of this player';
COMMENT ON COLUMN player.name IS 'Player name';
COMMENT ON COLUMN player.last_tick IS 'Last time tick occurred';


--
-- Inventory contains list of Item
-- Inventory is associated with a Player
--
CREATE TABLE inventory
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer, -- Player id of the owner of this item

  -- Energy
  energy bigint,

  -- Raw resources
  resources bigint,

  -- Processed materials
  materials bigint,

  --
  CONSTRAINT "FK_inventory_player__id" FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE inventory OWNER TO sawcog;
GRANT ALL ON inventory TO sawcog;
COMMENT ON COLUMN inventory.player__id IS 'Player id of the owner of this inventory';


--
-- Item
--
CREATE TABLE item
(
  id serial NOT NULL PRIMARY KEY,
  inventory__id integer, -- Inventory which owns this
  type integer, -- Item type
  quantity integer, -- Quantity of the item
  --
  CONSTRAINT "FK_item_inventory__id" FOREIGN KEY (inventory__id)
  REFERENCES inventory (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE item OWNER TO sawcog;
GRANT ALL ON item TO sawcog;
COMMENT ON COLUMN item.inventory__id IS 'Inventory id owner of this item';
COMMENT ON COLUMN item.type IS 'Item type';
COMMENT ON COLUMN item.quantity IS 'Quantity of the item';
