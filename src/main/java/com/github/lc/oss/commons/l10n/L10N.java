package com.github.lc.oss.commons.l10n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lc.oss.commons.util.IoTools;

public class L10N {
    private boolean useCache = true;

    private Map<java.util.Locale, Locale> locales = new HashMap<>();
    private Map<java.util.Locale, Boolean> definedLocales = new HashMap<>();

    public boolean isCaching() {
        return this.useCache;
    }

    public void setEnableCache(boolean value) {
        this.useCache = value;
    }

    public void clearCache() {
        this.locales.clear();
        this.definedLocales.clear();
    }

    public Map<String, String> getAll(java.util.Locale locale) {
        return this.getLocale(locale).getEntries().entrySet().parallelStream(). //
                collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));
    }

    public java.util.Locale getDefaultLocale() {
        return java.util.Locale.ENGLISH;
    }

    public Map<String, String> getPublicText(java.util.Locale locale, String idPrefix) {
        if (idPrefix == null) {
            return new HashMap<>();
        }

        String prefix = idPrefix.trim();
        if (prefix.equals("")) {
            return new HashMap<>();
        }

        return this.getLocale(locale).getEntries().entrySet().parallelStream(). //
                filter(e -> e.getValue().isPublic()). //
                filter(e -> e.getValue().getValue() != null). //
                filter(e -> e.getKey().startsWith(prefix)). //
                collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));
    }

    public String getText(java.util.Locale locale, String id, Variable... vars) {
        Entry e = this.getLocale(locale).getEntry(id);
        if (e == null) {
            return "??? " + id + " ???";
        }
        return Variable.replace(e.getValue(), vars);
    }

    public boolean hasLocale(java.util.Locale locale) {
        Boolean exists = this.definedLocales.get(locale);
        if (exists == null) {
            exists = !this.getJsonFiles(locale).isEmpty();
            this.definedLocales.put(locale, exists);
        }
        return exists;
    }

    public boolean hasText(java.util.Locale locale, String id) {
        Locale l = this.getLocale(locale);
        return l.getEntry(id) != null;
    }

    private Locale getLocale(java.util.Locale locale) {
        Locale l = this.locales.get(locale);
        if (l == null) {
            l = new Locale(locale, new HashMap<>());
            if (!this.getDefaultLocale().toLanguageTag().contentEquals(locale.toLanguageTag())) {
                /*
                 * Initialize locale with default locale values, then override as needed
                 */
                l.merge(this.getLocale(this.getDefaultLocale()), true);
            }

            Locale tmp;
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> textFiles = this.getJsonFiles(locale);
                textFiles.sort(String.CASE_INSENSITIVE_ORDER);
                for (String textFile : textFiles) {
                    tmp = mapper.readValue(IoTools.readAbsoluteFile(textFile), Locale.class);
                    l.merge(tmp, true);
                }

                textFiles = this.getExternalJsonFiles(locale);
                textFiles.sort(String.CASE_INSENSITIVE_ORDER);
                for (String textFile : textFiles) {
                    tmp = mapper.readValue(IoTools.readAbsoluteFile(textFile), Locale.class);
                    l.merge(tmp, false);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error parsing L10N files.", ex);
            }
            l.resolve();

            if (this.isCaching()) {
                this.locales.put(locale, l);
            }
        }
        return l;
    }

    private List<String> getJsonFiles(java.util.Locale locale) {
        String root = this.getAppL10NRoot();
        if (root == null) {
            throw new RuntimeException("L10N root cannot be null.");
        }

        root = root.trim();
        if (root.equals("")) {
            throw new RuntimeException("L10N root cannot be blank.");
        }

        if (!root.endsWith("/")) {
            root += "/";
        }

        return this.getFiles(root, locale);
    }

    private List<String> getExternalJsonFiles(java.util.Locale locale) {
        String root = this.getExternalL10NRoot();
        if (root == null) {
            return new ArrayList<>();
        }

        root = root.trim();
        if (root.equals("")) {
            return new ArrayList<>();
        }

        if (!root.endsWith("/")) {
            root += "/";
        }

        return this.getFiles(root, locale);
    }

    private List<String> getFiles(String root, java.util.Locale locale) {
        return IoTools.listDir(root + locale.toString().toLowerCase(), 5, path -> path.toString().endsWith(".json"));
    }

    protected String getAppL10NRoot() {
        return "text";
    }

    protected String getExternalL10NRoot() {
        return null;
    }
}
