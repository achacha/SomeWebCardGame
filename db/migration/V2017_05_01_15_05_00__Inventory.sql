--
-- Inventory is associated with a login
-- Inventory contains list of item
--
CREATE TABLE public.inventory
(
  id serial NOT NULL PRIMARY KEY,
  login__id integer, -- Login id of the owner of this item
  energy integer, -- Energy
  --
  CONSTRAINT inventory_login__id_fkey FOREIGN KEY (login__id)
  REFERENCES public.login (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE public.inventory OWNER TO sawcog;
COMMENT ON COLUMN public.inventory.login__id IS 'Login id of the owner of this inventory';
COMMENT ON COLUMN public.inventory.energy IS 'Energy total';


CREATE TABLE public.item
(
  id serial NOT NULL PRIMARY KEY,
  inventory__id integer, -- Inventory which owns this
  type integer, -- Item type
  quantity integer, -- Quantity of the item
  --
  CONSTRAINT item_inventory__id_fkey FOREIGN KEY (inventory__id)
  REFERENCES public.inventory (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.item OWNER TO sawcog;
COMMENT ON COLUMN public.item.inventory__id IS 'Inventory id owner of this item';
COMMENT ON COLUMN public.item.type IS 'Item type';
COMMENT ON COLUMN public.item.quantity IS 'Quantity of the item';
