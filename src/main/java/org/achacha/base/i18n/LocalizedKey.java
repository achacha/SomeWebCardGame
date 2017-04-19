package org.achacha.base.i18n;

/**
 * String key that needs to be localized
 * Serializes into JSON string and intended to be read-only
 */
public class LocalizedKey {
    private final String key;

    public String getKey() {
        return key;
    }

    public LocalizedKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return UIMessageHelper.getInstance().getLocalizedMsg(key);
    }
}
