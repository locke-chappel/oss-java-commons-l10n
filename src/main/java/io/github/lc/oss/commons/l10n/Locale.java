package io.github.lc.oss.commons.l10n;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
class Locale {
    protected static final Pattern REPLACEMENT = Pattern.compile("(#([^# '\"<>]+)#)");

    private final java.util.Locale locale;
    private final Map<String, Entry> entries;

    @JsonCreator
    Locale(@JsonProperty("locale") java.util.Locale locale, //
            @JsonProperty("entries") Map<String, Entry> entries) {
        this.locale = locale;
        this.entries = entries == null ? new HashMap<>() : entries;
    }

    java.util.Locale getLocale() {
        return this.locale;
    }

    Map<String, Entry> getEntries() {
        return Collections.unmodifiableMap(this.entries);
    }

    Entry getEntry(String id) {
        return this.entries.get(id);
    }

    void merge(Locale from, boolean detectDupliates) {
        if (detectDupliates) {
            Set<String> dupKeys = from.getEntries().keySet().parallelStream(). //
                    filter(this.entries.keySet()::contains). //
                    collect(Collectors.toSet());
            if (this.locale == from.getLocale() && !dupKeys.isEmpty()) {
                throw new RuntimeException(String.format("The following keys are duplicates: %s", //
                        dupKeys.parallelStream().collect(Collectors.joining(",", "[", "]"))));
            }
        }
        this.entries.putAll(from.getEntries());
    }

    void resolve() {
        this.getEntries().entrySet().parallelStream(). //
                forEach(e -> {
                    Entry updated = this.resolve(e.getKey(), e.getValue());
                    if (updated != e.getValue()) {
                        this.entries.put(e.getKey(), updated);
                    }
                });
    }

    private Entry resolve(String id, Entry entry) {
        String tmp = entry.getValue();
        if (tmp == null) {
            return entry;
        }

        Matcher matcher = Locale.REPLACEMENT.matcher(tmp);
        String replacementId;
        Entry replacement;
        boolean replaced = false;
        while (matcher.find()) {
            replacementId = matcher.group(2);
            if (id.contentEquals(replacementId)) {
                /* ignore self references */
                continue;
            }
            replacement = this.getEntries().get(replacementId);
            if (replacement != null) {
                if (Locale.REPLACEMENT.matcher(replacement.getValue()).find()) {
                    replacement = this.resolve(replacementId, replacement);
                }
                tmp = tmp.replaceAll(matcher.group(1), replacement.getValue());
                replaced = true;
            }
        }
        return replaced ? new Entry(tmp, entry.isPublic()) : entry;
    }
}
