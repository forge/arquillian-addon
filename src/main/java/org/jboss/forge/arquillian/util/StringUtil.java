package org.jboss.forge.arquillian.util;


public class StringUtil {

    // This is used in CLI display.
    public static String getStringForCLIDisplay(String s) {
        return s.toLowerCase().replace(" ", "-");
    }
}
