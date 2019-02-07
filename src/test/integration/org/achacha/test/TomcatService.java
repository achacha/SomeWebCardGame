package org.achacha.test;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.OneLineFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Start/stop embedded tomcat
 * Once instance globally
 */
/*
Specify correct XML/XSL jars?
-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl -Djavax.xml.transform.TransformerFactory=com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl

*/
public class TomcatService {
    private static final Logger LOGGER = LogManager.getLogger(TomcatService.class);

    private static final String TOMCAT_HOME = "tomcat/catalina.home/";
    private static final String TOMCAT_WORK = "build/tomcat/work/";
    static final String TOMCAT_BASE = "build/libs/exploded/SomeWebCardGame-1.0.1-SNAPSHOT.war";

    private static Thread thread;
    private static TomcatRunner tomcatRunner;

    public static boolean isWarValid() {
        File deployedWar = new File(TOMCAT_BASE);
        LOGGER.info("Deploying embedded tomcat with TOMCAT_BASE="+deployedWar.getAbsolutePath());
        return deployedWar.exists();
    }

    static class TomcatRunner implements Runnable {
        private static Tomcat tomcat;
        static boolean tomcatStarting;
        static boolean tomcatRunning;

        @Override
        public void run() {
            if (tomcatRunning)
                System.out.println("Tomcat is already running?");
            else {
                tomcatStarting = true;
                try {
                    // Flag this instance will be a test instance
                    System.setProperty("TEST_MODE", "1");

                    setRootLoggingLevel(Level.INFO);
                    System.out.println("===| Starting Tomcat");
                    tomcat = createEmbeddedTomcat();
                    System.out.println("===| Created Tomcat");
//                setRootLoggingLevel(Level.FINE);
                    tomcat.start();
//                setLogginLevel(Level.INFO);
                    System.out.println("===| Started Tomcat");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                tomcatRunning = true;
                tomcat.getServer().await();
            }
        }

        void stop() {
            try {
                System.out.println("===| Stopping Tomcat");
                tomcat.stop();
                tomcatRunning = false;
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

        boolean isTomcatRunning() {
            return tomcatRunning;
        }
    }

    public static boolean isTomcatRunning() {
        if (TomcatRunner.tomcatStarting) {
            System.out.println("Tomcat is starting...");
            // Wait to start
            while (!TomcatRunner.tomcatRunning) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("...waiting...");
                    System.out.flush();
                    e.printStackTrace();
                }
            }
        }

        return TomcatRunner.tomcatRunning;
    }

    public static synchronized void startTomcat() {
        if (!TomcatRunner.tomcatRunning) {
            System.out.println("+++| Start embedded Tomcat on port=" + BaseIntegrationTest.PORT + " base=" + TOMCAT_BASE);
            // Start embedded tomcat
            tomcatRunner = new TomcatRunner();
            thread = new Thread(tomcatRunner);
            thread.start();
            while (!tomcatRunner.isTomcatRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to start Tomcat", e);
                }
            }
            System.out.println("---| Start embedded Tomcat on port=" + BaseIntegrationTest.PORT + " base=" + TOMCAT_BASE);
        }
        else {
            System.out.println("---| Embedded Tomcat already running");
        }
    }

    private static Tomcat createEmbeddedTomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(TOMCAT_HOME);
        tomcat.setPort(BaseIntegrationTest.PORT);

        // Trigger creation of connector
        Connector connector = tomcat.getConnector();
        connector.setPort(BaseIntegrationTest.PORT);

        //Set StandardServer properties
        StandardServer server = (StandardServer)tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);

        // Configure context
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(TOMCAT_BASE).getAbsolutePath());
        System.out.println("configuring app with basedir: " + new File("./" + TOMCAT_BASE).getAbsolutePath());
        ctx.setParentClassLoader(BaseIntegrationTest.class.getClassLoader());
        ctx.setTldValidation(false);
        ctx.setWorkDir(TOMCAT_WORK);

        // Skip scanning XML/XSLT jars
        StandardJarScanner jarScanner = (StandardJarScanner)ctx.getJarScanner();
        StandardJarScanFilter jarScanFilter = (StandardJarScanFilter)jarScanner.getJarScanFilter();
        jarScanFilter.setPluggabilitySkip("xercesImpl*.jar,xml-apis*.jar,serializer*.jar");
        jarScanFilter.setTldSkip("xercesImpl*.jar,xml-apis*.jar,serializer*.jar");

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File(TOMCAT_BASE);
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        // Set StandardHost properties
        StandardHost stdHost = (StandardHost) tomcat.getHost();
        stdHost.setAppBase(TOMCAT_BASE);
        stdHost.setUnpackWARs(false);
        stdHost.setAutoDeploy(true);
        stdHost.setDeployOnStartup(true);

        return tomcat;
    }

    public static synchronized void stopTomcat() {
        if (tomcatRunner != null) {
            System.out.println("+++| Stop Tomcat");
            tomcatRunner.stop();
            while (tomcatRunner.isTomcatRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to stop Tomcat", e);
                }
            }
            tomcatRunner = null;
        }
        System.out.println("---| Stop Tomcat");
    }

    public static void setRootLoggingLevel(Level level) throws UnsupportedEncodingException {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("");
        logger.setLevel(level);
        Handler[] handlers = logger.getHandlers();
        Handler handler;
        if (handlers.length == 1 && handlers[0] instanceof ConsoleHandler) {
            handler = handlers[0];
        } else {
            handler = new ConsoleHandler();
        }
        handler.setFormatter(new OneLineFormatter());
        handler.setLevel(level);
        handler.setEncoding("UTF-8");
        logger.addHandler(handler);
    }

}
