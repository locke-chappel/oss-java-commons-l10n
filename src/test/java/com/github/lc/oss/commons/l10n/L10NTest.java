package com.github.lc.oss.commons.l10n;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.lc.oss.commons.testing.AbstractTest;

public class L10NTest extends AbstractTest {
    @Test
    public void test_hasLocale() {
        L10N l10n = new L10N();

        Assertions.assertTrue(l10n.hasLocale(java.util.Locale.ENGLISH));
        Assertions.assertTrue(l10n.hasLocale(java.util.Locale.GERMAN));
        Assertions.assertFalse(l10n.hasLocale(java.util.Locale.FRENCH));

        /* answers are cached */
        Assertions.assertTrue(l10n.hasLocale(java.util.Locale.ENGLISH));
        Assertions.assertTrue(l10n.hasLocale(java.util.Locale.GERMAN));
        Assertions.assertFalse(l10n.hasLocale(java.util.Locale.FRENCH));
    }

    @Test
    public void test_hasText() {
        L10N l10n = new L10N();

        Assertions.assertTrue(l10n.hasText(java.util.Locale.ENGLISH, "one"));
        Assertions.assertTrue(l10n.hasText(java.util.Locale.GERMAN, "one"));
        Assertions.assertTrue(l10n.hasText(java.util.Locale.FRENCH, "one"));

        Assertions.assertFalse(l10n.hasText(java.util.Locale.ENGLISH, "junk"));
        Assertions.assertFalse(l10n.hasText(java.util.Locale.GERMAN, "junk"));
        Assertions.assertFalse(l10n.hasText(java.util.Locale.FRENCH, "junk"));
    }

    @Test
    public void test_getAll() {
        L10N l10n = new L10N();

        Map<String, String> all = l10n.getAll(java.util.Locale.ENGLISH);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(3, all.size());
        Assertions.assertEquals("a", all.get("one"));
        Assertions.assertEquals("b", all.get("two"));
        Assertions.assertEquals("English", all.get("default"));
        Assertions.assertNull(all.get("ONE"));
        Assertions.assertNull(all.get("TwO"));
        Assertions.assertNull(all.get("Default"));

        all = l10n.getAll(java.util.Locale.CHINESE);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(3, all.size());
        Assertions.assertEquals("a", all.get("one"));
        Assertions.assertEquals("b", all.get("two"));
        Assertions.assertEquals("English", all.get("default"));
        Assertions.assertNull(all.get("ONE"));
        Assertions.assertNull(all.get("TwO"));
        Assertions.assertNull(all.get("Default"));

        all = l10n.getAll(java.util.Locale.GERMAN);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(4, all.size());
        Assertions.assertEquals("a", all.get("one"));
        Assertions.assertEquals("b", all.get("two"));
        Assertions.assertEquals("a b #test# %var%", all.get("test"));
        Assertions.assertEquals("German", all.get("default"));
        Assertions.assertNull(all.get("ONE"));
        Assertions.assertNull(all.get("TwO"));
        Assertions.assertNull(all.get("tEst"));
        Assertions.assertNull(all.get("Default"));
    }

    @Test
    public void test_cached() {
        L10N l10n = new L10N();
        l10n.setEnableCache(false);

        Assertions.assertFalse(l10n.isCaching());
        final String before = l10n.getText(java.util.Locale.ENGLISH, "one");
        Assertions.assertEquals("a", before);

        l10n.setEnableCache(true);
        Assertions.assertTrue(l10n.isCaching());

        final String after = l10n.getText(java.util.Locale.ENGLISH, "one");
        final String again = l10n.getText(java.util.Locale.ENGLISH, "one");
        Assertions.assertEquals("a", after);
        Assertions.assertEquals("a", again);
        Assertions.assertNotSame(before, after);
        Assertions.assertSame(after, again);

        l10n.clearCache();
        Assertions.assertTrue(l10n.isCaching());

        final String afterAgain = l10n.getText(java.util.Locale.ENGLISH, "one");
        Assertions.assertEquals("a", afterAgain);
        Assertions.assertNotSame(again, afterAgain);
    }

    @Test
    public void test_notFound() {
        L10N l10n = new L10N();

        Assertions.assertEquals("??? not found ???", l10n.getText(java.util.Locale.CHINESE, "not found"));
    }

    @Test
    public void test_badJson() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return "junk";
            }
        };

        try {
            l10n.getText(java.util.Locale.ENGLISH, "id");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error parsing L10N files.", ex.getMessage());
        }
    }

    @Test
    public void test_nullL10NRoot() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return null;
            }
        };

        try {
            l10n.getText(java.util.Locale.ENGLISH, "id");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("L10N root cannot be null.", ex.getMessage());
        }
    }

    @Test
    public void test_emptyL10NRoot() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return "";
            }
        };

        try {
            l10n.getText(java.util.Locale.ENGLISH, "id");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("L10N root cannot be blank.", ex.getMessage());
        }
    }

    @Test
    public void test_blankL10NRoot() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return " \t \r \n \t ";
            }
        };

        try {
            l10n.getText(java.util.Locale.ENGLISH, "id");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("L10N root cannot be blank.", ex.getMessage());
        }
    }

    @Test
    public void test_chaining() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return "chain/";
            }

            @Override
            protected String getExternalL10NRoot() {
                return "junk-path/";
            }
        };

        Assertions.assertEquals("a b", l10n.getText(java.util.Locale.ENGLISH, "a"));
        Assertions.assertEquals("b", l10n.getText(java.util.Locale.ENGLISH, "b"));
        Assertions.assertEquals("c a b b d a b", l10n.getText(java.util.Locale.ENGLISH, "c"));
        Assertions.assertEquals("d a b", l10n.getText(java.util.Locale.ENGLISH, "d"));
        Assertions.assertEquals("#e#", l10n.getText(java.util.Locale.ENGLISH, "e"));
        Assertions.assertEquals("#z#", l10n.getText(java.util.Locale.ENGLISH, "f"));
        Assertions.assertEquals("g c a b b d a b", l10n.getText(java.util.Locale.ENGLISH, "g"));
    }

    @Test
    public void test_getPublicText() {
        L10N l10n = new L10N() {
            @Override
            protected String getAppL10NRoot() {
                return "public/";
            }

            @Override
            protected String getExternalL10NRoot() {
                return " \r \t \n \t  ";
            }
        };

        Map<String, String> result = l10n.getPublicText(java.util.Locale.ENGLISH, null);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        result = l10n.getPublicText(java.util.Locale.ENGLISH, "");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        result = l10n.getPublicText(java.util.Locale.ENGLISH, " ");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        result = l10n.getPublicText(java.util.Locale.ENGLISH, " \t \r \n \t ");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        result = l10n.getPublicText(java.util.Locale.ENGLISH, " prefix ");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("A", result.get("prefix.value.subvalue"));

        result = l10n.getPublicText(java.util.Locale.ENGLISH, "prefix ");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("A", result.get("prefix.value.subvalue"));

        result = l10n.getPublicText(java.util.Locale.ENGLISH, "prefix.");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("A", result.get("prefix.value.subvalue"));

        result = l10n.getPublicText(java.util.Locale.ENGLISH, "prefix .");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        result = l10n.getPublicText(java.util.Locale.ENGLISH, "other");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("F", result.get("other"));
    }

    @Test
    public void test_external_l10n() {
        L10N l10n = new L10N();

        L10N l10nExt = new L10N() {
            @Override
            protected String getExternalL10NRoot() {
                return "text-ext";
            }
        };

        Assertions.assertEquals("a", l10n.getText(java.util.Locale.ENGLISH, "one"));
        Assertions.assertEquals("English", l10n.getText(java.util.Locale.ENGLISH, "default"));
        Assertions.assertEquals("English", l10nExt.getText(java.util.Locale.ENGLISH, "default"));
        Assertions.assertEquals("alpha", l10nExt.getText(java.util.Locale.ENGLISH, "one"));
        Assertions.assertEquals("Extra! Extra!", l10nExt.getText(java.util.Locale.ENGLISH, "extra"));
    }
}
