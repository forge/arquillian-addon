package test.integration.support.assertions;


import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.configuration.Configuration;

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
}
