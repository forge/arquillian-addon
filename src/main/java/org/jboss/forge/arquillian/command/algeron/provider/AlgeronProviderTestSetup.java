package org.jboss.forge.arquillian.command.algeron.provider;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.List;
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
    JavaClassSource updateTest(JavaClassSource test, String provider, Function<JavaClassSource, String> testBody);

    static void removeStaticMethodWithDeploymentAnnotation(JavaClassSource test) {
        final List<MethodSource<JavaClassSource>> methods = test.getMethods();

        methods.forEach(method -> {
            if (method.hasAnnotation("Deployment") && method.isStatic()) {
                test.removeMethod(method);
            }
        });
    }
}
