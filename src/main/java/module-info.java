module com.github.lc.oss.commons.l10n {
    requires com.github.lc.oss.commons.util;

    requires com.fasterxml.jackson.databind;

    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.annotation;

    opens com.github.lc.oss.commons.l10n to com.fasterxml.jackson.databind;

    exports com.github.lc.oss.commons.l10n;
}
