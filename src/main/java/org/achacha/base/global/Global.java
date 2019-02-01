package org.achacha.base.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.achacha.base.cache.DboCacheManager;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.i18n.LocalizedKey;
import org.achacha.base.i18n.LocalizedKeyAdapter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

public abstract class Global {
    protected static final Logger LOGGER = LogManager.getLogger(Global.class);
    private static final String DEFAULT_INSTANCE_PROPERTIES_FILE = ".sawcog.properties";

    private static Global instance = null;

    /**
     * @return Global singleton
     */
    public static Global getInstance() {
        return instance;
    }

    /**
     * Set method allows instance constructor to be controlled by user
     *
     * @param instanceToUse Global
     */
    public static void setInstance(Global instanceToUse) {
        instance = instanceToUse;
    }

    /**
     * Properties
     */
    protected GlobalProperties properties = new GlobalProperties();

    /**
     * Database manager
     */
    protected DatabaseManager databaseManager;

    /** Gson for convert to/from Json, pretty version does user indented formatting */
    protected Gson gson;
    protected Gson gsonPretty;

    /**
     * @return Build information
     */
    public static JsonObject getBuildVersion() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("Vendor", Global.class.getPackage().getImplementationVendor());
        jobj.addProperty("Title", Global.class.getPackage().getImplementationTitle());
        jobj.addProperty("Version", Global.class.getPackage().getImplementationVersion());
        return jobj;
    }

    public enum Mode {
        DEV,
        PRODUCTION
    }

    protected Mode mode = Mode.DEV;

    /**
     * Name of the application creating this object
     * Controls DB initialization
     */
    protected String application;

    /**
     * Filename of the properties file to use when bootstrapping this instance
     */
    protected String instancePropertyFile;

    /**
     * Get hostname of this machine
     */
    private static String HOSTNAME;

    static {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            LOGGER.error("Failed to get hostname, defaulting to localhost", uhe);
            HOSTNAME = "localhost";
        }
    }

    public static String getHOSTNAME() {
        return HOSTNAME;
    }

    /**
     * @return String application name
     */
    public String getApplication() {
        return application;
    }

    /**
     * Constructor called when object created during startup
     * @param application String name
     * @param instancePropertiesFile Filename for properties for this instance
     */
    protected Global(String application, String instancePropertiesFile) {
        LOGGER.info("+++[0]+++ Global executeSql started: "+application);
        this.application = application;
        this.instancePropertyFile = StringUtils.defaultString(instancePropertiesFile, DEFAULT_INSTANCE_PROPERTIES_FILE);

        // Server Default Timezone is always GMT.
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        // Use SSL v3 and v2 and TLS v1
        System.setProperty("https.protocols", "SSLv3,SSLv2Hello,TLSv1");

        // Load properties file
        File file = new File(FilenameUtils.concat(System.getProperty("user.home"), this.instancePropertyFile));
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                properties.load(fis);
            } catch (IOException ioe) {
                throw new RuntimeException("Failed to read: " + file, ioe);
            } finally {
                // Remember to close the input stream.
                if (fis != null) try {
                    fis.close();
                } catch (Exception e) {
                    LOGGER.warn("Failed to close file when reading properties", e);
                }
            }
        } else {
            throw new RuntimeException("Properties missing from home directory: " + file);
        }
        LOGGER.info("+ Global properties loaded from: " + file);

        // Detect mode of operation
        String modeParam = properties.getProperty("mode").toUpperCase();
        switch (modeParam) {
            case "PRODUCTION":
                LOGGER.warn("PRODUCTION mode detected");
                mode = Mode.PRODUCTION;
                break;

            default:
                LOGGER.info("DEV mode detected");
        }

        // Set catalina.base in development so that /log directory is not required
        if (null == System.getProperty("catalina.base")) {
            System.setProperty("catalina.base", System.getProperty("user.home"));
        }

        initGson();

        LOGGER.info("+++[1]+++ Global object created");
    }

    /**
     * @return true if in development mode
     */
    public boolean isDevelopment() {
        return mode == Mode.DEV;
    }

    /**
     * @return true if in production mode
     */
    public boolean isProduction() {
        return mode == Mode.PRODUCTION;
    }

    /**
     * Init called when ServletConfig is available during initialization
     *
     * @param servletContextEvent ServletContextEvent if null then unit testing
     */
    public void init(ServletContextEvent servletContextEvent) {
        LOGGER.info("+++[2]+++ Global starting init");

        initDatabaseManager();
        initConstants(servletContextEvent);

        LOGGER.info("+++[3]+++ Global initialized");

        initChild();

        LOGGER.info("+++[4]+++ Global child initialized");

        LOGGER.warn(application + " instance started [" + HOSTNAME + "] at [" + LocalDateTime.now().toString() + "]");
    }

    public abstract void initChild();

    /**
     * Allow specific initialization of DatabaseManager based on use-case
     * Properties have been loaded from machine specific config file during construction
     */
    public abstract void initDatabaseManager();

    /**
     * Initialize global constants
     * @param servletContextEvent ServletContextEvent
     */
    protected void initConstants(ServletContextEvent servletContextEvent) {
        LOGGER.debug("+ Global properties overlay from database global_properties table");
        GlobalPropertiesHelper.load(properties, null);    // Load base
        GlobalPropertiesHelper.load(properties, application);        // Load application specific

        properties.processServletContextEvent(servletContextEvent);

        LOGGER.debug("+ Loading DboCaches");
        DboCacheManager.getInstance().init();
    }

    /**
     * Init Gson
     */
    protected void initGson() {
        LOGGER.debug("+ Initializing Gson");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalizedKey.class, new LocalizedKeyAdapter());
        gson = gsonBuilder.create();
        gsonPretty = gsonBuilder.setPrettyPrinting().create();

        LOGGER.debug("+ Initializing JsonPath for Gson");
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new GsonJsonProvider();
            private final MappingProvider mappingProvider = new GsonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    /**
     * Explicit shutdown to cleanup any resources that may need it
     */
    public static void shutdown() {
        LOGGER.info("---[ 1 ]--- Global shutdown started");
        LOGGER.info("---[ 0 ]--- Global shutdown finished");
    }

    /**
     * @return Properties specific to this Global instance to be used in migration and admin connections
     */
    public abstract Properties getDbProperties();

    /**
     * @return DatabaseManager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * This Gson is intended for all JSON based operations
     * @return Gson configured
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * This Gson is intended for formatted user facing JSON (mostly in admin based calls)
     * @return Gson configured for pretty printing
     * @see #getGson()
     */
    public Gson getGsonPretty() {
        return gsonPretty;
    }

    public GlobalProperties getProperties() {
        return properties;
    }

    public Mode getMode() {
        return mode;
    }
}
