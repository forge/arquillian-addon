package test.integration.support.assertions;


import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.jboss.forge.addon.configuration.Configuration;

import java.util.Arrays;

public class ConfigurationAssert extends AbstractAssert<ConfigurationAssert, Configuration> {

    public ConfigurationAssert(Configuration actual) {
        super(actual, ConfigurationAssert.class);
    }

    public static ConfigurationAssert assertThat(Configuration actual) {
        return new ConfigurationAssert(actual);
    }

    public ConfigurationAssert withProperty(String key, String value) {
        String property = (String) actual.getProperty(key);

        Assertions.assertThat(property).isEqualTo(value);
        return this;
    }

    public ConfigurationAssert withProperties(String... keyValues) {
        SoftAssertions softAssertions = new SoftAssertions();

        for (String s : Arrays.asList(keyValues)) {
            final String[] keyValue = s.split(":");
            String property = (String) actual.getProperty(keyValue[0]);
            softAssertions.assertThat(property).isEqualTo(keyValue[1]);
        }

        softAssertions.assertAll();
        return this;
    }
}
