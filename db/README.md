ONE-TIME Configuration
---
Postgres: Development database
  - Install postgres
  	It is best to do this using a package manager (see ROOT/config/README.md)
  	
  - Create user sawcog:cogsaw
  	to do this manually, type the following commands into your terminal
  		
  		- sudo su postgres (this sets you as the postgres user)
  		- psql (this launches the postgres command line)
  		- CREATE USER sawcog WITH PASSWORD 'cogsaw';
  	
  	alternatively, use a postgres tool like pgadmin3
  	
  - Alter user sawcog so that it has CREATEDB permission
  	to do this manually, type the following into the postgres command line:
  	- ALTER USER sawcog WITH CREATEDB;
  	
  - Create database necropolis and assign it to user sawcog
  	to do this manually, type the following into the postgres command line:
  	- CREATE DATABASE necropolis WITH OWNER sawcog;
  	- CREATE DATABASE necropolis_test WITH OWNER sawcog;
  
  - Test connection to verify that sawcog user can login and use necropolis database
  	
  	psql -d necropolis -U sawcog -W -h localhost


Configuring postgresql to allow tcpip connection (assumed v9.5+, adjust as needed)
---
<pre>
sudo vi /etc/postgresql/9.5/main/postgresql.conf
</pre>

Add following (to listen on all available interfaces):
listen_addresses = '*'


<pre>
sudo vi /etc/postgresql/9.5/main/pg_hba.conf
</pre>

Add following to allow other machines to login (this will need to be removed/limited if deployed to production):<br/>
<pre>
host     all     all     0.0.0.0/0     md5
</pre>

<pre>
sudo service postgresql restart
</pre>


Using gradle (To see all tasks: ./gradlew tasks)
---
  - **flywayMigrate -i** - Execute migrations
  - **flywayClean -i**  - Clean the database and drop the table associated with migrations
