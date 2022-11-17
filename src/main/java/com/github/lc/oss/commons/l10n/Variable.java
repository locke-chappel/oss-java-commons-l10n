package com.github.lc.oss.commons.l10n;

import java.util.regex.Pattern;

public class Variable {
    public static final Pattern HTML_ID = Pattern.compile("(%([^% '\"<>]+)%)");
    public static final String DELIMITER = "%";

    public static String replace(String src, Variable... vars) {
        if (src == null || src.trim().equals("") || vars == null) {
            return src;
        }
        String tmp = src;
        for (Variable var : vars) {
            if (var != null) {
                tmp = var.replace(tmp);
            }
        }
        return tmp;
    }

    private final String key;
    private final String value;

    public Variable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getKeyId() {
        return Variable.DELIMITER + this.key + Variable.DELIMITER;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }

    public String replace(String src) {
        if (src == null || src.trim().equals("") || this.getKey() == null || this.getValue() == null) {
            return src;
        }

        return src.replaceAll(this.getKeyId(), this.getValue());
    }
}
