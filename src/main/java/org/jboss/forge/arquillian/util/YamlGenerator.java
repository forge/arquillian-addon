package org.jboss.forge.arquillian.util;

import org.yaml.snakeyaml.Yaml;

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

    public static String toYml(Map<String, Object> params) {
        Yaml yaml = new Yaml();
        final String dump = yaml.dump(params);

        // If value contains line separator then generator string will contain ` |-`
        // which we don't want in result as cube yaml parser is not able to parse it.
         return dump.replace(" |-", "").replace(" |","").trim();
    }

}
