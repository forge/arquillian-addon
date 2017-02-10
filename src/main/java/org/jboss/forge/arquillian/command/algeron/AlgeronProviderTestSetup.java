package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.function.Function;

/**
 * Algeron Provider contract to modify given test to be a contract provider test.
 */
public interface AlgeronProviderTestSetup {

    /**
     * updates test with provider code.
     * @param test class.
     * @param provider name.
     * @param testBody Sets the body of the test method. If it is null, implementators should provide a default body.
     */
    void updateTest(JavaClassSource test, String provider, Function<JavaClassSource, String> testBody);

}
