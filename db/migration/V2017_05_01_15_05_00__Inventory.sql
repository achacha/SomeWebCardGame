--
-- Player associated with login
-- Inventory is associated with a player
-- Inventory contains list of item
--
CREATE TABLE player
(
  id serial NOT NULL PRIMARY KEY,
  login__id integer, -- Login id of the owner of this item
  --
  CONSTRAINT player_login__id_fkey FOREIGN KEY (login__id)
  REFERENCES login (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE player OWNER TO sawcog;
COMMENT ON COLUMN player.login__id IS 'Login id of the owner of this player';


CREATE TABLE inventory
(
  id serial NOT NULL PRIMARY KEY,
  player__id integer, -- Player id of the owner of this item
  energy integer, -- Energy
  --
  CONSTRAINT inventory_player__id_fkey FOREIGN KEY (player__id)
  REFERENCES player (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE inventory OWNER TO sawcog;
COMMENT ON COLUMN inventory.player__id IS 'Player id of the owner of this inventory';
COMMENT ON COLUMN inventory.energy IS 'Energy total';


CREATE TABLE item
(
  id serial NOT NULL PRIMARY KEY,
  inventory__id integer, -- Inventory which owns this
  type integer, -- Item type
  quantity integer, -- Quantity of the item
  --
  CONSTRAINT item_inventory__id_fkey FOREIGN KEY (inventory__id)
  REFERENCES inventory (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE item OWNER TO sawcog;
COMMENT ON COLUMN item.inventory__id IS 'Inventory id owner of this item';
COMMENT ON COLUMN item.type IS 'Item type';
COMMENT ON COLUMN item.quantity IS 'Quantity of the item';
