-- NOTE: This should be executed after manually inserting ids into tables
-- Since we need certain IDs for unit testing and posgres does not increment a sequence unless it needs a new one
--   we are stuck doing this to work around that strange behavior
-- @see: https://stackoverflow.com/questions/9108833/postgres-autoincrement-not-updated-on-explicit-id-inserts
SELECT setval('login_id_seq', (SELECT MAX(id) from public.login));
SELECT setval('inventory_id_seq', (SELECT MAX(id) from public.inventory));
SELECT setval('card_id_seq', (SELECT MAX(id) from public.encounter));
SELECT setval('player_id_seq', (SELECT MAX(id) from public.player));
SELECT setval('adventure_id_seq', (SELECT MAX(id) from public.adventure));
SELECT setval('encounter_id_seq', (SELECT MAX(id) from public.encounter));
