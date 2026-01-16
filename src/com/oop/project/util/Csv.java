package com.oop.project.util;

import java.util.ArrayList;
import java.util.List;

public final class Csv {
    private Csv() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        String v = value;
        boolean needsQuotes = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        if (v.contains("\"")) {
            v = v.replace("\"", "\"\"");
        }
        return needsQuotes ? ("\"" + v + "\"") : v;
    }

    public static List<String> parseLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null) {
            return out;
        }
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == ',') {
                    out.add(current.toString());
                    current.setLength(0);
                } else if (c == '"') {
                    inQuotes = true;
                } else {
                    current.append(c);
                }
            }
        }
        out.add(current.toString());
        return out;
    }
}
