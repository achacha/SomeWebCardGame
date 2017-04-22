package org.achacha.base.i18n;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

/**
 * I18n related functions
 */
public class I18nHelper {
    private static final Logger LOGGER = LogManager.getLogger(I18nHelper.class);

    /**
     * Get Locale from string name
     * @param loc String as language_country_variant
     * @return Locale
     */
    public static Locale getLocale(String loc) {
        if (StringUtils.isEmpty(loc)) {
            LOGGER.warn("Locale string is empty or null, falling back to default");
            return Locale.getDefault();
        }

        String[] locparts = loc.split("_");
        switch (locparts.length) {
            case 3:
                return new Locale(locparts[0], locparts[1], locparts[2]);
            case 2:
                return new Locale(locparts[0], locparts[1]);
            case 1:
                return new Locale(locparts[0]);
            default:
                LOGGER.error("Unknown locale: " + loc);
                return Locale.getDefault();
        }

    }
}
