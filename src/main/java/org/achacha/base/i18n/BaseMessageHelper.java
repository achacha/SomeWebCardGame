package org.achacha.base.i18n;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class BaseMessageHelper {
    private static final Logger LOGGER = LogManager.getLogger(BaseMessageHelper.class);

    // The base name of the all error message properties files.
    private static final Utf8Control utf8Contol = new Utf8Control();
    
    protected Utf8Control getUtf8Control(){return utf8Contol;}
    
    public abstract String getBundleName();
    
    /** Get the localized error message using the user's current locale.  A non-null
     * string is always returned.  When a bundle cannot be loaded, the empty string
     * is returned.  When a key is not found, than the offending key is returned in 
     * a string.  Neither of these errors should happen in production.
     * 
     * @param key the error message key
     * @param parms optional values for filling placeholders in value string 
     * @return the localized message
     */
    public String getLocalizedMsg(String key, Object... parms)
    {
        return getLocalizedMsg(getUserLocale(), key, parms);
    }
    
    /** Get the localized error message using the specified locale.  A non-null
     * string is always returned.  When a bundle cannot be loaded, the empty string
     * is returned.  When a key is not found, than the offending key is returned in 
     * a string.  Neither of these errors should happen in production. 
     * 
     * @param key the error message key
     * @param parms optional values for filling placeholders in value string 
     * @return the localized message
     */
    public String getLocalizedMsg(Locale locale, String key, Object... parms)
    {
        // ResourceBundle manages its own caches so we don't have to preload bundles.
        ResourceBundle bundle = ResourceBundle.getBundle(getBundleName(), locale, utf8Contol);
        if (bundle == null)
        {
            LOGGER.error("Unable to load bundle " + getBundleName() + " with locale " +
                    locale.getDisplayName() + "");
            return "";
        }
        
        // Get the value of the key.
        String value = null;
        try {value = bundle.getString(key);}
         catch (MissingResourceException e)
          {
             return "Key \"" + key + "\" not found in bundle " + getBundleName() + "";
          }
        
        // We found a value that might have placeholders.  If we start getting
        // IllegalArgumentExceptions here, we can wrap the format call in a try block.
        if (parms.length > 0) value = MessageFormat.format(value, parms);  
        return value;
    }
    
    /**
     * Get user locale for this login, if not logged in use client provided,
     * then fall back to default.
     * @return Locale
     */
    public Locale getUserLocale() {
        CallContext context = CallContextTls.get();
        if (null == context) {
//            if (LOGGER.isDebugEnabled())
//                LOGGER.debug("Message resolution is missing CallContext (possibly called in JSP before login), using default Locale");
            return Locale.getDefault();
        }

        Locale locale = null;

        if (null != context.getLogin()) {
            locale = context.getLogin().getLocale();
        }

        // If still null, try request locale from client/browser
        if (null == locale) {
            locale = context.getRequest().getLocale();
        }

        // If still null, use default
        if (null == locale) {
            locale = Locale.getDefault();
        }

        return locale;
    }
}
