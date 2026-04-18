package com.abitoymanagement.backend.util;

import java.util.ArrayList;
import java.util.List;

public final class DelimitedTextUtil {

    private static final char DELIMITER = '|';
    private static final char ESCAPE_CHARACTER = '\\';

    private DelimitedTextUtil() {
    }

    public static String join(List<String> fields) {
        List<String> escapedFields = new ArrayList<>();
        for (String field : fields) {
            escapedFields.add(escape(field));
        }
        return String.join(String.valueOf(DELIMITER), escapedFields);
    }

    public static List<String> split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean escaping = false;

        for (char character : line.toCharArray()) {
            if (escaping) {
                if (character == 'n') {
                    currentField.append('\n');
                } else if (character == 'r') {
                    currentField.append('\r');
                } else {
                    currentField.append(character);
                }
                escaping = false;
                continue;
            }

            if (character == ESCAPE_CHARACTER) {
                escaping = true;
            } else if (character == DELIMITER) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (escaping) {
            currentField.append(ESCAPE_CHARACTER);
        }

        fields.add(currentField.toString());
        return fields;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
