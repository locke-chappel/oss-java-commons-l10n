package com.github.lc.oss.commons.l10n;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.lc.oss.commons.testing.AbstractTest;

public class LocaleTest extends AbstractTest {
    @Test
    public void test_constructor_nulls() {
        Locale l = new Locale(null, null);
        Assertions.assertNull(l.getLocale());
        Assertions.assertNotNull(l.getEntries());
        Assertions.assertTrue(l.getEntries().isEmpty());
    }

    @Test
    public void test_merge_sameLocale() {
        Map<String, Entry> entriesA = new HashMap<>();
        entriesA.put("a", null);
        Map<String, Entry> entriesB = new HashMap<>();
        entriesB.put("b", null);
        Locale a = new Locale(java.util.Locale.ENGLISH, entriesA);
        Locale b = new Locale(java.util.Locale.ENGLISH, entriesB);

        Assertions.assertEquals(1, a.getEntries().size());
        Assertions.assertTrue(a.getEntries().containsKey("a"));
        Assertions.assertFalse(a.getEntries().containsKey("b"));

        Assertions.assertEquals(1, b.getEntries().size());
        Assertions.assertFalse(b.getEntries().containsKey("a"));
        Assertions.assertTrue(b.getEntries().containsKey("b"));

        a.merge(b, true);

        Assertions.assertEquals(2, a.getEntries().size());
        Assertions.assertTrue(a.getEntries().containsKey("a"));
        Assertions.assertTrue(a.getEntries().containsKey("b"));

        Assertions.assertEquals(1, b.getEntries().size());
        Assertions.assertFalse(b.getEntries().containsKey("a"));
        Assertions.assertTrue(b.getEntries().containsKey("b"));
    }

    @Test
    public void test_merge_duplicateKeys() {
        Map<String, Entry> entries = new HashMap<>();
        entries.put("a", null);
        Locale a = new Locale(java.util.Locale.ENGLISH, entries);
        Locale b = new Locale(java.util.Locale.ENGLISH, entries);

        Assertions.assertEquals(1, a.getEntries().size());
        Assertions.assertTrue(a.getEntries().containsKey("a"));

        Assertions.assertEquals(1, b.getEntries().size());
        Assertions.assertTrue(b.getEntries().containsKey("a"));

        try {
            a.merge(b, true);
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("The following keys are duplicates: [a]", ex.getMessage());
        }

        Assertions.assertEquals(1, a.getEntries().size());
        Assertions.assertTrue(a.getEntries().containsKey("a"));

        Assertions.assertEquals(1, b.getEntries().size());
        Assertions.assertTrue(b.getEntries().containsKey("a"));
    }
}
