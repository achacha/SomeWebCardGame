Some Web Card Game (for a lack of a better name)

Mostly used as a base project to integrate various technologies.

Requirements
---
- JDK 11 or newer
- Tomcat 9.0.36 or newer
- Nodejs via yarn/npm
- Git bash shell (if using windows)


Project specs
---
- Gradle build integrated with IntelliJ
- NPM via gradle


Server specs
---
- WAR based Java REST server
- Jersey JAX-RS


Client
---
- NPM package.json via yarn
- PhaserJS UI library


Install via npm into package.json
---
1. `npm install --save axios`
1. `npm install --save tether bootstrap font-awesome`
1. `npm install --save vue`

1. Set in package.json at scripts:`"build": "webpack"`
    _(This allows us to use `npm run build`)_

1. Then inside IntelliJ:  https://www.jetbrains.com/help/idea/using-webpack.html


Install phaser dependencies into package.json
---
1. `npm install --save jquery popper.js phaser-ce`


Building
---
Import IntelliJ project from Gradle
Run tasks:
    
    # Webpack Vue app
    ./gradlew npmWebpack
    # Phaser app (to be moved into webpack)
    ./gradlew copyJs
  
copyJs will move needed JavaScipt libraries from /node_modules to /src/main/webapp/js, 
it only needs to run after updates to package.json and will call npmInstall as
 a dependency.
 
 
Troubleshooting
===
**org.hsqldb.HsqlException: Database lock acquisition failure**
-
IntelliJ is still connected to the DB, select Database view and disconnect it 
  
**Nothing is showing up when going to http://localhost:8080/**
-
Check if src/main/webapp/js exists, if not run **copyJs** gradle task
 
**Clicking on Gradle task "npmInstall" shows "env: node: No such file or directory"**
-
This is due to node living in /usr/local/bin/node and for some odd reason /usr/local/bin is not
in IntelliJ tool path.  Run this from command line if needed but that step is not really
needed inside IntelliJ since we are building artifact inside the IDE.

**global_properties relation is missing**
-
Unit test config is probably specifying $MODULE_DIR$ and it should be blank to default to project base dir

**Can't log in**
-
If this is an initial data import:
 - default login superuser login is su:test, this password must be changed
 - default sample user login is alex:test, this password must be changed
