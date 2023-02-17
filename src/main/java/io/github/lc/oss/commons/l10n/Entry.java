package io.github.lc.oss.commons.l10n;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
class Entry {
    private final String value;
    private final boolean isPublic;

    @JsonCreator
    Entry(@JsonProperty(value = "value", defaultValue = "") String value, //
            @JsonProperty(value = "public", defaultValue = "false") boolean isPublic) {
        this.value = value;
        this.isPublic = isPublic;
    }

    String getValue() {
        return this.value;
    }

    boolean isPublic() {
        return this.isPublic;
    }
}
