package org.jboss.forge.arquillian.command.cube;


import io.fabric8.kubernetes.api.model.Service;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;


public class KubernetesCubeTestSetup implements CubeTestSetup {

    private String serviceName;

    public KubernetesCubeTestSetup(String serviceName) {
        this.serviceName = serviceName;
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
        if (!test.hasMethodSignature("public service_instance_should_not_be_null() : void")) {
            test.addMethod()
                .setName("service_instance_should_not_be_null")
                .setPublic()
                .setReturnTypeVoid();
        }

        test.getMethod("service_instance_should_not_be_null")
            .setBody("assertNotNull(service);")
            .addAnnotation("Test");
    }


    private void addImports(JavaClassSource test) {
        test.addImport(ArquillianResource.class);
        test.addImport("org.arquillian.cube.kubernetes.annotations.Named");
        test.addImport(Service.class);
        test.addImport("org.junit.Assert.assertNotNull").setStatic(true);
    }

    private void createEnrichments(JavaClassSource test) {
        test.addField()
            .setName("service")
            .setType(Service.class)
            .setPrivate()
            .addAnnotation(ArquillianResource.class);

        test.getField("service").addAnnotation("Named").setStringValue(serviceName);
    }
}
