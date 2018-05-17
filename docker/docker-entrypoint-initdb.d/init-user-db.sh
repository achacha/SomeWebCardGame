#!/usr/bin/env bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE ROLE sawcog;
    ALTER ROLE sawcog WITH NOSUPERUSER INHERIT CREATEROLE LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'cogsaw';
    ALTER USER sawcog WITH CREATEDB;
    CREATE DATABASE necropolis WITH OWNER sawcog;
    GRANT ALL PRIVILEGES ON DATABASE necropolis TO sawcog;
    \connect necropolis;
    ALTER SCHEMA public OWNER TO sawcog;
    CREATE DATABASE necropolis_test WITH OWNER sawcog;
    GRANT ALL PRIVILEGES ON DATABASE necropolis_test TO sawcog;
    \connect necropolis_test;
    ALTER SCHEMA public OWNER TO sawcog;
EOSQL