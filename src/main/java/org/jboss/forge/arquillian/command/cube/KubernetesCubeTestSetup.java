package org.jboss.forge.arquillian.command.cube;


import io.fabric8.kubernetes.api.model.Service;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;


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
        removeTestMethod("should_be_deployed", test);

        return test;
    }

    private void createTestMethod(JavaClassSource test) {
        if (!test.hasMethodSignature("public serviceInstanceShouldNotBeNull() : void")) {
            test.addMethod()
                .setName("serviceInstanceShouldNotBeNull")
                .setPublic()
                .setReturnTypeVoid();
        }

        test.getMethod("serviceInstanceShouldNotBeNull")
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
