package io.github.lc.oss.commons.l10n;

import java.util.Locale;

public class UserLocale {
    private Locale locale;

    public UserLocale() {
        this.locale = Locale.ENGLISH;
    }

    public UserLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
