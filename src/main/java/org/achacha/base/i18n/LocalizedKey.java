package org.achacha.base.i18n;

/**
 * String key that needs to be localized
 * Serializes into JSON string and intended to be read-only
 */
public class LocalizedKey implements LocalizedKeyResource {
    private final String key;

    public LocalizedKey(String key) {
        this.key = key;
    }

    /**
     * Factory method
     * @param key String resource key
     * @return LocalizedKey
     */
    public static LocalizedKey of(String key) {
        return new LocalizedKey(key);
    }

    public String getKey() {
        return key;
    }

    /**
     * @return Localized resource value
     */
    @Override
    public String toString() {
        return UIMessageHelper.getInstance().getLocalizedMsg(key);
    }
}
