package org.jboss.forge.arquillian.command.cube;


import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.net.URL;

public class DockerCubeTestSetup implements CubeTestSetup {

    private String containerName;
    private String exposedPort;

    public DockerCubeTestSetup(String containerName, String exposedPort) {
        this.containerName = containerName;
        this.exposedPort = exposedPort;
    }

    @Override
    public JavaClassSource updateTest(JavaClassSource test) {
        addImports(test);
        createEnrichments(test);
        createTestMethod(test);

        return test;
    }

    private void createTestMethod(JavaClassSource test) {
        if (!test.hasMethodSignature("public dockerUrlShouldNotBeNull() : void")) {
            test.addMethod()
                .setName("dockerUrlShouldNotBeNull")
                .setPublic()
                .setReturnTypeVoid();
        }

        test.getMethod("dockerUrlShouldNotBeNull")
            .setBody("assertNotNull(url);")
            .addAnnotation("Test");
    }

    private void addImports(JavaClassSource test) {
        test.addImport(ArquillianResource.class);
        test.addImport("org.arquillian.cube.DockerUrl");
        test.addImport("org.junit.Assert.assertNotNull").setStatic(true);
    }

    private void createEnrichments(JavaClassSource test) {
        test.addField()
            .setName("url")
            .setType(URL.class)
            .setPrivate()
            .addAnnotation(ArquillianResource.class);

        test.getField("url").addAnnotation("org.arquillian.cube.DockerUrl")
            .setStringValue("containerName", containerName)
            .setLiteralValue("exposedPort", exposedPort);
    }
}
