package org.achacha.base.i18n;

import com.google.gson.JsonObject;
import org.achacha.base.json.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class UIMessageHelper extends BaseMessageHelper {
    private static final Logger LOGGER = LogManager.getLogger(UIMessageHelper.class);
    
    // The base property file name for the messages managed by this class.
    public static final String BUNDLE_NAME = "Messages";
    
    // Singleton instance.
    private static UIMessageHelper instance; 
    
    private UIMessageHelper(){}
    
    /**
     * Get the singleton instance of this class.
     * @return singleton
     */
    public static synchronized UIMessageHelper getInstance()
    {
       if (instance == null) instance = new UIMessageHelper(); 
       return instance;
    }
    
    @Override
    public String getBundleName(){return BUNDLE_NAME;} 

    /**
     * Iterate message keys and add to model based on current user's locale
     * In model name, the . is converted to _ and I18N is prepended (a.b.c becomes I18N_a_b_c)
     * @param model Map
     * @param messageNames String array of keys
     */
    public void addMessagesToModel(Map<String, String> model, String[] messageNames) {

        for (int i=0; i<messageNames.length; ++i) {
            String value = getLocalizedMsg(messageNames[i]);
            if (null != value) {
                model.put("i18n_"+messageNames[i].replace('.','_'), value);
            }
            else {
                LOGGER.warn("Unable to find message resource entry: {}", messageNames[i]);
            }
        }
    }
    
    /**
     * Get default messages as a JSON object
     * @return JsonObject
     */
    public JsonObject getDefaultMessages() {
        ResourceBundle defaultBundle = 
                ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), getUtf8Control());
        JsonObject messages = new JsonObject();
        for (String keyMessage : defaultBundle.keySet()) {
            messages.addProperty(keyMessage, defaultBundle.getString(keyMessage));
        }

        JsonObject obj = JsonHelper.getSuccessObject();
        obj.add(JsonHelper.DATA, messages);
        return obj;
    }

    /**
     * Get messages as JSON object where default is overlayed with requested locale
     * @param requestedLocale Locale
     * @return JsonObject
     */
    public JsonObject getLocalizedMessages(Locale requestedLocale) {
        // Build object for this locale
        JsonObject messages = new JsonObject();
        ResourceBundle defaultBundle = 
                ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), getUtf8Control());
        ResourceBundle localizedBundle = 
                ResourceBundle.getBundle(BUNDLE_NAME, requestedLocale, getUtf8Control());
        
        if (null == localizedBundle) {
            // No such language
            for (String keyMessage : defaultBundle.keySet()) {
                messages.addProperty(keyMessage, defaultBundle.getString(keyMessage));
            }
        }
        else {
            // User default and overlay the localized
            for (String keyMessage : defaultBundle.keySet()) {
                if (localizedBundle.containsKey(keyMessage)) {
                    // Use localized message
                    messages.addProperty(keyMessage, localizedBundle.getString(keyMessage));
                } else {
                    // Fall back on default
                    messages.addProperty(keyMessage, defaultBundle.getString(keyMessage));
                }
            }
        }

        JsonObject obj = JsonHelper.getSuccessObject();
        obj.add(JsonHelper.DATA, messages);
        return obj;
    }
}
