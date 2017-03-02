package org.jboss.forge.arquillian.command.algeron.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import org.arquillian.algeron.pact.consumer.spi.Pact;
import org.arquillian.algeron.pact.consumer.spi.PactVerification;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.HashMap;
import java.util.Map;

public class PactAlgeronConsumerTestSetup implements AlgeronConsumerTestSetup {

    private static final String FRAGMENT_METHOD = "" +
        "final Map<String, String> headers = new HashMap<>();" + System.lineSeparator() +
        "headers.put(\"Content-Type\", \"application/json\");" + System.lineSeparator() +
        System.lineSeparator() +
        "      return builder" + System.lineSeparator() +
        "              .uponReceiving(\"%s\")" + System.lineSeparator() +
        "              .path(\"/\")" + System.lineSeparator() +
        "              .method(\"GET\")" + System.lineSeparator() +
        "              .headers(headers)" + System.lineSeparator() +
        "              .willRespondWith()" + System.lineSeparator() +
        "              .status(200)" + System.lineSeparator() +
        "              .body(\"\")" + System.lineSeparator() +
        "              .toFragment();";

    @Override
    public JavaClassSource updateTest(JavaClassSource test, String consumer, String provider, String fragmentName) {

        addImports(test);
        annotateTestClass(test, consumer, provider);
        createContractFragment(test, fragmentName);
        createTestMethod(test, consumer, provider, fragmentName);

        return test;
    }

    private void createTestMethod(JavaClassSource test, String consumer, String provider, String fragmentName) {
        final MethodSource<JavaClassSource> javaClassSourceMethodSource = test.addMethod().setName(generateTestMethodName(consumer, provider, fragmentName));
        javaClassSourceMethodSource
            .setPublic()
            .setReturnTypeVoid()
            .setBody("")
            .addAnnotation("Test");

        try {
            javaClassSourceMethodSource.addAnnotation(PactVerification.class)
                .setStringValue(PactVerification.class.getMethod("fragment").getName(),
                    fragmentName);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private String generateTestMethodName(String consumer, String provider, String fragmentName) {
        return String.format("should_%s_between_%s_and_%s", deCamelCasealize(fragmentName, '_'), consumer, provider);
    }

    private void createContractFragment(JavaClassSource test, String fragmentName) {
        test.addMethod()
            .setName(fragmentName)
            .setPublic()
            .setBody(String.format(FRAGMENT_METHOD, deCamelCasealize(fragmentName, ' ')))
            .setReturnType(PactFragment.class)
            .addParameter(PactDslWithProvider.class, "builder");
    }

    private void annotateTestClass(JavaClassSource test, String consumer, String provider) {
        try {
            test.addAnnotation(Pact.class)
                .setStringValue(Pact.class.getMethod("consumer").getName(), consumer)
                .setStringValue(Pact.class.getMethod("provider").getName(), provider);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addImports(JavaClassSource test) {
        test.addImport(Map.class);
        test.addImport(HashMap.class);
        test.addImport(Pact.class);
        test.addImport(PactVerification.class);
        test.addImport(PactFragment.class);
        test.addImport(PactDslWithProvider.class);
    }

    private String deCamelCasealize(String camelCasedString, char replace) {
        return camelCasedString.replaceAll("(\\p{Ll})(\\p{Lu})", "$1" + replace + "$2");
    }

}
