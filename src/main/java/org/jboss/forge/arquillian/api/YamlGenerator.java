package org.jboss.forge.arquillian.api;

import java.util.Map;
import java.util.stream.Collector;

public class YamlGenerator {

    /**
     * Converts a map to String.
     * Currently only Strings as value is supported.
     *
     * @param params to convert.
     * @return String with YAML format
     */
    public static String toYaml(Map<String, String> params) {
        return params.entrySet().stream().collect(Collector.of(StringBuilder::new,
            (stringBuilder, entry) ->
                stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(System.lineSeparator()),
            StringBuilder::append,
            StringBuilder::toString));
    }

}
