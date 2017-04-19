package org.achacha.base.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This class is here only to override a deficiency in the way Java reads properties
 * files using its ResourceBundle class. The problem is that the Java class treats all
 * property files as ISO-8859-1 encodings. If the files actually are UTF-8, then there
 * are at least two choices.
 * 
 * One choice is to convert each value from ISO-8859-1 to UTF-8 after it's read. The
 * code would look something like:
 * 
 * value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
 * 
 * Another approach is to override the ResourceBundle.Control.newBundle() method and
 * specify UTF-8 explicitly as the encoding to be used when reading the properties
 * file. This approach was chosen because it's more efficient than re-encoding each
 * value.
 * 
 * Other approaches include those that don't use ResourceBundle, but then we lose
 * the caching and other features of that class.
 * 
 * For more background:
 * 
 * http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle
 * 
 */
public class Utf8Control extends ResourceBundle.Control 
{
        /** This method is copied from the Java 8 distribution and one line was changed
         * (see below).  New versions of Java might require updates to this method.
         * We also added the warning suppression annotation on the next line of code.
         */
        @SuppressWarnings("PMD")
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                        ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;
            if (format.equals("java.class")) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends ResourceBundle> bundleClass
                        = (Class<? extends ResourceBundle>)loader.loadClass(bundleName);

                    // If the class isn't a ResourceBundle subclass, throw a
                    // ClassCastException.
                    if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
                        bundle = bundleClass.newInstance();
                    } else {
                        throw new ClassCastException(bundleClass.getName()
                                     + " cannot be cast to ResourceBundle");
                    }
                } catch (ClassNotFoundException e) {
                }
            } else if (format.equals("java.properties")) {
                final String resourceName = toResourceName(bundleName, "properties");
                final ClassLoader classLoader = loader;
                final boolean reloadFlag = reload;
                InputStream stream = null;
                try {
                    stream = AccessController.doPrivileged(
                        new PrivilegedExceptionAction<InputStream>() {
                            public InputStream run() throws IOException {
                                InputStream is = null;
                                if (reloadFlag) {
                                    URL url = classLoader.getResource(resourceName);
                                    if (url != null) {
                                        URLConnection connection = url.openConnection();
                                        if (connection != null) {
                                            // Disable caches to get fresh data for
                                            // reloading.
                                            connection.setUseCaches(false);
                                            is = connection.getInputStream();
                                        }
                                    }
                                } else {
                                    is = classLoader.getResourceAsStream(resourceName);
                                }
                                return is;
                            }
                        });
                } catch (PrivilegedActionException e) {
                    throw (IOException) e.getException();
                }
                if (stream != null) {
                    try {
                        // ******** THIS IS THE ONLY LINE OF CODE WE CHANGED ********
                        bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                    } finally {
                        stream.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            return bundle;
        }
}