package test.integration.cube;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class CubeAddTestCommandTest extends ShellTestTemplate {

    @Test
    public void should_create_test_for_kubernetes() throws TimeoutException, FileNotFoundException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/kubernetes.yml")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/kubernetes.yml");

        shell().execute("arquillian-create-test --named MyKubernetesTest --target-package org.cube.kubernetes")
            .execute("arquillian-cube-add-test --test-class org.cube.kubernetes.MyKubernetesTest --service-name my-service");

        final JavaClassSource testClass = extractClass(project, "org.cube.kubernetes.MyKubernetesTest");

        assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");

        assertThat(testClass).hasMethod("service_instance_should_not_be_null");
        assertThat(testClass).hasField("service").annotatedWith(ArquillianResource.class).ofType("io.fabric8.kubernetes.api.model.Service");
        assertThat(testClass).hasField("service").annotatedWithStringValue("Named", "my-service");
    }

    @Test
    public void should_create_test_for_docker() throws TimeoutException, FileNotFoundException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/Dockerfile")
            .execute("arquillian-cube-setup --type docker --file-path src/test/resources/Dockerfile");

        shell().execute("arquillian-create-test --named MyDockerTest --target-package org.cube.docker")
            .execute("arquillian-cube-add-test --test-class org.cube.docker.MyDockerTest --exposed-port 8080 --container-name hello-world");

        final JavaClassSource testClass = extractClass(project, "org.cube.docker.MyDockerTest");

        assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");

        assertThat(testClass).hasMethod("docker_url_should_not_be_null");
        assertThat(testClass).hasField("url").annotatedWith(ArquillianResource.class).ofType("java.net.URL");
        assertThat(testClass).hasField("url").hasAnnotation("DockerUrl").withPropertyAndValue("containerName", "hello-world").withPropertyAndValue("exposedPort","8080");
    }

    @Test
    public void should_create_test_for_docker_compose() throws TimeoutException, FileNotFoundException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch docker-compose.yml")
            .execute("arquillian-cube-setup --type docker-compose --file-path docker-compose.yml");

        shell().execute("arquillian-create-test --named MyDockerComposeTest --target-package org.cube.docker")
            .execute("arquillian-cube-add-test --test-class org.cube.docker.MyDockerComposeTest  --exposed-port 8080 --container-name hello-world");

        final JavaClassSource testClass = extractClass(project, "org.cube.docker.MyDockerComposeTest");

        assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");

        assertThat(testClass).hasMethod("docker_url_should_not_be_null");
        assertThat(testClass).hasField("url").annotatedWith(ArquillianResource.class).ofType("java.net.URL");
        assertThat(testClass).hasField("url").hasAnnotation("DockerUrl").withPropertyAndValue("containerName", "hello-world").withPropertyAndValue("exposedPort", "8080");
    }

    @Test
    public void should_create_test_for_openshift() throws TimeoutException, FileNotFoundException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/hello-pod.yml")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/hello-pod.yml");

        shell().execute("arquillian-create-test --named MyOpenshiftTest --target-package org.cube.openshift")
            .execute("arquillian-cube-add-test --service-name my-service --test-class org.cube.openshift.MyOpenshiftTest --service-name my-service");

        final JavaClassSource testClass = extractClass(project, "org.cube.openshift.MyOpenshiftTest");

        assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");

        assertThat(testClass).hasMethod("service_instance_should_not_be_null");
        assertThat(testClass).hasField("service").annotatedWith(ArquillianResource.class).ofType("io.fabric8.kubernetes.api.model.Service");
        assertThat(testClass).hasField("service").annotatedWithStringValue("Named", "my-service");
    }

}
