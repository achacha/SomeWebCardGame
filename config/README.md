# Preface
<p>
 <b>~/opt</b> is where all external software controlled by user will live<br/>
 this includes JDK, tools, IDE, application server (since IDE starts it in debug mode), etc<br/>
 this excludes OS auto=started services like database server, web server<br/>
 all development done on IntelliJ IDEA, if you use anything else you are responsible for project definition, etc<br/>
</p>

<br/>

# Create a VM for the database and other services
 - Download VirtualBox and install (You can use VMWare if you like but you should know how to set it up)
 - Download *buntu ISO of choice (I use xubuntu or lubuntu for their light weight)
 - When creating new VM from ISO set HD space to 20GB+ to be sure you have room for the future, 10GB is minimum
 - You may want to adjust settings and use Bidirectional clipboard so you can copy/paste from inside the VM
 - You need 2 network adapters
      - Host-only to allow your host machine to reach it
      - Bridged to allow it to connect to the internet
 - Once VM starts go to Devices | Insert Guest Additions CD Image... (this will insert the CD)
 - Open shell (or Terminal Emulator or whatever the distro calls it)
 - <pre>
    cd /media/fjord/VBOXADDITIONS_[whatever version]
    sudo ./VBoxLinuxAdditions.run
 </pre>
   
 - Now you should be able to resize the window and have the VM match the size (after you restart bit later) 
 - Then from shell do: ifconfig to get the IP so you can alias it, in your hosts file add something like:
    - `192.168.0.233   fjord-db`
    - You should be able to `ping fjord-db` from host machine
    - Linux/osx: `/etc/hosts`, windows `C:\Windows\System32\drivers\etc\hosts` 

<br/>

# Update and install software on VM 
 - Update your linux to latest
    - `sudo apt-get update`
    - `sudo apt-get dist-upgrade`
 - Install postgres and admin tool
    - `sudo apt-get install postgresql postgresql-contrib pgadmin3`

 - Restart the VM
<br/>

# Configure postgres on your VM
<br/>
From shell window set admin password to `postgres`:
<pre>
sudo -u postgres psql postgres

<i>postgres=# </i>\password
postgres
</pre>

NOTE: **From here follow the directions in /db/README.md**

<br/>
<hr/>
<br/>

# Create user specific directory on local machine
<pre>
mkdir ~/opt 
cd opt
</pre>

<hr/>
# Software required locally

<i>download and extract latest JDK to ~/opt and symlink it - <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">http://www.oracle.com/technetwork/java/javase/downloads/index.html</a></i>

Depending on your OS you can either symlink it like below or install it.

<pre>
 ln -s jdk-[version] jdk
</pre>

<br/>
<hr/>
<br/>

<i>download and extract latest Tomcat (version 8.5.x or newer) - <a href="http://tomcat.apache.org/">http://tomcat.apache.org/</a></i>
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