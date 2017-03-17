package org.jboss.forge.arquillian.command.algeron.provider;

import org.arquillian.algeron.pact.provider.spi.Provider;
import org.arquillian.algeron.pact.provider.spi.Target;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

public class PactAlgeronProviderTestSetup implements AlgeronProviderTestSetup {

    @Override
    public JavaClassSource updateTest(JavaClassSource test, String provider, Function<JavaClassSource, String> testBody) {
        addImports(test);
        annotateTestClass(test, provider);
        createEnrichments(test);
        createTestMethod(test, testBody);
        removeTestMethod("should_be_deployed", test);

        return test;
    }

    private void createTestMethod(JavaClassSource test, Function<JavaClassSource, String> testBody) {
        test.addMethod()
            .setPublic()
            .setReturnTypeVoid()
            .setName("should_verify_contract")
            .setBody(getTestMethodBody(test, testBody))
            .addAnnotation("Test");
    }

    private String getTestMethodBody(JavaClassSource test, Function<JavaClassSource, String> testBody) {

        if (testBody != null) {
            return testBody.apply(test);
        }

        return String.format("target.testInteraction(%s);", getUrlEnriched(test).isPresent() ? getUrlEnriched(test).get() : "");
    }

    private Optional<String> getUrlEnriched(JavaClassSource test) {
        return test.getFields()
            .stream()
            .filter(fieldSource -> fieldSource.hasAnnotation(ArquillianResource.class)
                && fieldSource.getType().isType(URL.class))
            .map(FieldSource::getName)
            .findFirst();
    }

    private void addImports(JavaClassSource test) {
        test.addImport(Provider.class);
        test.addImport(ArquillianResource.class);
        test.addImport(Target.class);
    }

    private void annotateTestClass(JavaClassSource test, String provider) {
        test.addAnnotation(Provider.class)
            .setStringValue(provider);
    }

    private void createEnrichments(JavaClassSource test) {
        test.addField()
            .setName("target")
            .setType(Target.class)
            .setPrivate()
            .addAnnotation(ArquillianResource.class);
    }

}
