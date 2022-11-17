package com.github.lc.oss.commons.l10n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.lc.oss.commons.testing.AbstractTest;

public class UserLocaleTest extends AbstractTest {
    @Test
    public void test_constructors() {
        UserLocale ul = new UserLocale();
        Assertions.assertEquals(java.util.Locale.ENGLISH, ul.getLocale());

        ul = new UserLocale(null);
        Assertions.assertNull(ul.getLocale());

        ul = new UserLocale(java.util.Locale.GERMAN);
        Assertions.assertEquals(java.util.Locale.GERMAN, ul.getLocale());
    }

    @Test
    public void test_setLocale() {
        UserLocale ul = new UserLocale();
        Assertions.assertEquals(java.util.Locale.ENGLISH, ul.getLocale());

        ul.setLocale(java.util.Locale.GERMAN);
        Assertions.assertEquals(java.util.Locale.GERMAN, ul.getLocale());
    }
}
