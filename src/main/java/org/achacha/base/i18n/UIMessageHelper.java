package org.achacha.base.i18n;

import com.google.gson.JsonObject;
import org.achacha.base.json.JsonHelper;

import java.util.Locale;
import java.util.ResourceBundle;

public class UIMessageHelper extends BaseMessageHelper {
    // The base property file name for the messages managed by this class.
    private static final String BUNDLE_NAME = "Messages";

    // Singleton instance.
    private static UIMessageHelper instance = new UIMessageHelper();

    private UIMessageHelper() {
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return singleton
     */
    public static UIMessageHelper getInstance() {
        return instance;
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Get default messages as a JSON object
     *
     * @return JsonObject
     */
    public JsonObject getDefaultMessages() {
        ResourceBundle defaultBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
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
     *
     * @param requestedLocale Locale
     * @return JsonObject
     */
    public JsonObject getLocalizedMessages(Locale requestedLocale) {
        // Build object for this locale
        JsonObject messages = new JsonObject();
        ResourceBundle defaultBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
        ResourceBundle localizedBundle = ResourceBundle.getBundle(BUNDLE_NAME, requestedLocale);

        if (null == localizedBundle) {
            // No such language
            for (String keyMessage : defaultBundle.keySet()) {
                messages.addProperty(keyMessage, defaultBundle.getString(keyMessage));
            }
        } else {
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
