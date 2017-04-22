# Preface
<p>
 <b>~/opt</b> is where all external software controlled by user will live<br/>
 this includes JDK, tools, IDE, application server (since IDE starts it in debug mode), etc<br/>
 this excludes OS auto=started services like database server, web server<br/>
 all development done on IntelliJ IDEA, if you use anything else you are responsible for project definition, etc<br/>
</p>

<br/>
<hr/>
<br/>

<h3>Create a VM for the database and other services</h3>
VirtualBox or similar with a flavor or ubuntu can be used.  Once created, install postgresql.<br/>
Add alias to your /etc/hosts file for the new VM called 'fjord-db'.  The purpose of this is to not have postgres
run on your dev machine all the time, just when you need it.<br/>
<pre>
sudo apt-get install postgresql postgresql-contrib
</pre>
<br/>

# Configure postgres
<br/>
<pre>
sudo -u postgres psql postgres

<i>postgres=# </i>\password
postgres
</pre>

<br/>
<hr/>
<br/>

<h3>Create user specific directory</h3>
<pre>
mkdir ~/opt 
cd opt
</pre>

<hr/>
<h1>Software</h1>

<i>download and extract latest JDK to ~/opt and symlink it - <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">http://www.oracle.com/technetwork/java/javase/downloads/index.html</a></i>

<pre>
 ln -s jdk-[version] jdk
</pre>

<br/>
<hr/>
<br/>

<i>download and extract latest Tomcat (version 8.5.4 or newer) - <a href="http://tomcat.apache.org/">http://tomcat.apache.org/</a></i>
<pre>
 ln -s apache-tomcat-[version] tomcat
</pre>

<br/>
<hr/>
<br/>


# Environment
<b>
Once you have downloaded and symlinked the above
Edit ~/.profile and add following
</b>
   
<pre>
JAVA_HOME=$HOME/opt/jdk
PATH="$HOME/opt/jdk/bin:$HOME/opt/jdk/jre/bin:$PATH"
</pre>

<br/>
<h1> Machine specific property file </h1>

Copy HOME/.fjord.properties to $HOME/.fjord.properties<br/>
  - $HOME is your home directory<br/>

<br/>
<h1> Building </h1>
Clone the repo from git (assuming $HOME/Code/fjord from now on)<br/>
<pre>
ant init
</pre>
This will create:<br/>
build/catalina_home - This is the project specific location to be used by IDE

# IDEA: config
(IMPORTANT: On Mac "IntelliJ IDEA | Preferences..." is same as "File | Settings..." on linux and windows)

Preferences... | Build, Execution, Deployment | Compiler | Annotation Processing 
  - Enable annotation processing
  - Store generated sources relative to: Module Content Root


# IDEA: Version Control
After project opens it will tell you that Git root is not registered, click "Add root" or go to
Preferences | Version Control 


# IDEA: Tomcat config
1. Run | Edit Configurations... | + | Tomcat | Local
1. Application Server | Configure...
  - Click +
  - Tomcat Home: ~/opt/tomcat  (this is where tomcat is actually installed/located)
  - Name: Give it some useful name
3. Uncheck "After Launch" to not start a browser every time you start server
1. Click 'Deployment'
1. + | Artifact...  (it will add main automatically since it is the only artifact you have configured)

# IDEA: More project structure after tomcat config
Project Structure | Project Settings | Modules | main | Dependencies
1. + | Library | Application Server | [your tomcat]

Project Structure | Project Settings | Modules | test | Dependencies
1. + | Library | Application Server | [your tomcat]


Flyway
-
Flyway will do all the migrations for you.  Flyway uses a cache for the migration files, so if it seems to be stuck with stale files run flywayClean.
flywayMigrate does all the work.