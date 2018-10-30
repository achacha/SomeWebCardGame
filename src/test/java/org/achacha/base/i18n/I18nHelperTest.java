package org.achacha.base.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class I18nHelperTest {
    @Test
    void testGetLocale() {
        Locale locale = I18nHelper.getLocale("en-us");
        assertEquals("en-us", locale.getLanguage());
    }
}
