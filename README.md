Some Web Card Game (for a lack of a better name)

Mostly used as a base project to integrate various technologies.

Requirements
---
JDK 8.0 or newer
Tomcat 8.5 or newer
Nodejs and NPM
Git bash shell (if using windows)


Project
---
Gradle build integrated with IntelliJ
NPM via gradle


Server
---
WAR based Java REST server
Jersey JAX-RS
Hibernate DB layer
Slf4j binding to JDK14 in Tomcat


Client
---
NPM package.json
PhaserJS UI library


Building
---
Import IntelliJ project from Gradle
Run tasks:
    
    ./gradlew npmSetup
    ./gradlew copyJs
  
copyJs will move needed JavaScipt libraries from /node_modules to /src/main/webapp/js, 
it only needs to run after updates to package.json and will call npmInstall as
 a dependency.
 
 
Trobleshooting
---
**org.hsqldb.HsqlException: Database lock acquisition failure**
IntelliJ is still connected to the DB, select Database view and disconnect it 

  
**Nothing is showing up when going to http://localhost:8080/**
Check if src/main/webapp/js exists, if not run **copyJs** gradle task 