package org.jboss.forge.arquillian.util;


public class StringUtil {

    // This is used in CLI display & to store properties in forge settings.
    public static String getStringForCLIDisplay(String s) {
        return s.toLowerCase().replace(" ", "-");
    }
}
