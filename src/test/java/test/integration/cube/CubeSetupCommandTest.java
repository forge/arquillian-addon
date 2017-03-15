package test.integration.cube;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class CubeSetupCommandTest extends ShellTestTemplate {

    @Test
    public void should_setup_arquillian_cube_for_docker() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/Dockerfile")
            .execute("arquillian-create-test --named MyDockerTest --target-package org.cube.docker")
            .execute("arquillian-cube-setup --type docker --file-path src/test/resources/Dockerfile");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainers",
            "version: '2'\n" +
                "    services:\n" +
                "      containerName:\n" +
                "        build:\n" +
                "          context: src/test/resources");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_without_docker_file_name() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/Dockerfile1")
            .execute("arquillian-cube-setup --type docker --file-path src/test/resources/Dockerfile1");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainers",
            "version: '2'\n" +
                "    services:\n" +
                "      containerName:\n" +
                "        build:\n" +
                "          context: src/test/resources\n" +
                "          dockerfile: Dockerfile1");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_with_docker_machine() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/Dockerfile")
            .execute("arquillian-cube-setup --type docker --file-path src/test/resources/Dockerfile --docker-machine-name dev");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("machineName", "dev");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainers",
            "version: '2'\n" +
                "    services:\n" +
                "      containerName:\n" +
                "        build:\n" +
                "          context: src/test/resources");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_compose() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch docker-compose.yml")
            .execute("arquillian-cube-setup --type docker-compose --file-path docker-compose.yml");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile", "docker-compose.yml");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_compose_with_docker_machine() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch docker-compose.yml")
            .execute("arquillian-cube-setup --type docker-compose --file-path docker-compose.yml --docker-machine-name dev1");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperties("dockerContainersFile:docker-compose.yml", "machineName:dev1");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/kubernetes.json")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/kubernetes.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("kubernetes");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes_with_resource_not_in_classpath() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch kubernetes.yml")
            .execute("arquillian-cube-setup --type kubernetes --file-path kubernetes.yml");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("kubernetes");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes_with_different_name_resource() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/kubernetes_1.json")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/kubernetes_1.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("kubernetes").withProperty("env.config.resource.name", "kubernetes_1.json");
    }

    @Test
    public void should_setup_arquillian_cube_for_openshift() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("touch src/test/resources/openshift.json")
            .execute("arquillian-cube-setup --type openshift --file-path src/test/resources/openshift.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-openshift").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("openshift").withProperty("env.config.resource.name", "openshift.json");
    }

}
