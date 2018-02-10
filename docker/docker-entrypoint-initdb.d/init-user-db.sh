#!/usr/bin/env bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER sawcog WITH PASSWORD 'cogsaw';
    ALTER USER sawcog WITH CREATEDB;
    CREATE DATABASE necropolis WITH OWNER sawcog;
    CREATE DATABASE necropolis_test WITH OWNER sawcog;
EOSQL