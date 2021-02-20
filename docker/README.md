Create a docker image and start container
---
Use docker/Dockerfile to build docker image

`docker build -t postgres_sawcog docker`
`docker run --name sawcog_container -e POSTGRES_PASSWORD=cogsaw -d -p 5432:5432 postgres_sawcog`


Create container based on image
---
Add _-p 5432:5432_ to expose postgresql port when starting container


Modify machine config property file
---
In **~/.sawcog.properties**:

db.jdbcUrl=jdbc:postgresql://localhost:5432/necropolis

test.db.jdbcUrl=jdbc:postgresql://localhost:5432/necropolis_test
