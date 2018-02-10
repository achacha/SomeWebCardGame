FROM postgres:latest
ENV POSTGRES_USER sawcog
ENV POSTGRES_PASSWORD cogsaw
ENV POSTGRES_DB necropolis
COPY docker-entrypoint-initdb.d/init-user-db.sh /docker-entrypoint-initdb.d/
EXPOSE 5432

