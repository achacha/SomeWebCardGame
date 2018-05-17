package org.achacha.base.i18n;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class I18nHelperTest {
    @Test
    public void testGetLocale() {
        Locale locale = I18nHelper.getLocale("en-us");
        Assert.assertEquals("en-us", locale.getLanguage());
    }
}
