package org.jboss.forge.arquillian.command.cube;


import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

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
        removeShouldBeDeployedMethod(test);

        return test;
    }

    private void removeShouldBeDeployedMethod(JavaClassSource test) {
        final MethodSource<JavaClassSource> should_be_deployed = test.getMethod("should_be_deployed");
        if (test.hasMethod(should_be_deployed)) {
            test.removeMethod(should_be_deployed);
        }
    }

    private void createTestMethod(JavaClassSource test) {
        if (!test.hasMethodSignature("public docker_url_should_not_be_null() : void")) {
            test.addMethod()
                .setName("docker_url_should_not_be_null")
                .setPublic()
                .setReturnTypeVoid();
        }

        test.getMethod("docker_url_should_not_be_null")
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
