module io.github.lc.oss.commons.l10n {
    requires io.github.lc.oss.commons.util;

    requires com.fasterxml.jackson.databind;

    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.annotation;

    opens io.github.lc.oss.commons.l10n to com.fasterxml.jackson.databind;

    exports io.github.lc.oss.commons.l10n;
}
