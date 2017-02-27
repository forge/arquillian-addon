package test.integration.cube;


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.FileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(containing = ShellTestTemplate.class)
public class CubeSetupCommandTest extends ShellTestTemplate {

    @Test
    public void should_setup_arquillian_cube_for_docker() throws Exception {

        final FileResource<?> dockerFile = extractTestResource(project, "dockerFile");
        dockerFile.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type docker --file-path src/test/resources/ --docker-file-name dockerFile");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("definitionFormat", "CUBE");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile",
            "containerName:\n" +
                "      buildImage:\n" +
                "        dockerfileLocation: src/test/resources/\n" +
                "        noCache: true\n" +
                "        remove: true\n" +
                "        dockerfileName: dockerFile");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_without_docker_file_name() throws Exception {

        final FileResource<?> dockerFile = extractTestResource(project, "dockerFile1");
        dockerFile.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit");

        shell().execute("arquillian-cube-setup --type docker --file-path src/test/resources/dockerFile1");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("definitionFormat", "CUBE");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile",
            "containerName:\n" +
                "      buildImage:\n" +
                "        dockerfileLocation: src/test/resources/dockerFile1\n" +
                "        noCache: true\n" +
                "        remove: true");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_with_docker_machine() throws Exception {

        final FileResource<?> dockerFile = extractTestResource(project, "dockerFile");
        dockerFile.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit");

        shell().execute("arquillian-cube-setup --type docker --file-path src/test/resources/ --docker-file-name dockerFile --docker-machine --machine-name dev");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("definitionFormat", "CUBE");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("machineName", "dev");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile",
            "containerName:\n" +
                "      buildImage:\n" +
                "        dockerfileLocation: src/test/resources/\n" +
                "        noCache: true\n" +
                "        remove: true\n" +
                "        dockerfileName: dockerFile");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_compose() throws Exception {

        shell().execute("echo docker-compose > docker-compose.yml");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type docker-compose --file-path docker-compose.yml");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile", "docker-compose.yml");
    }

    @Test
    public void should_setup_arquillian_cube_for_docker_compose_with_docker_machine() throws Exception {

        shell().execute("echo docker-compose > docker-compose.yml");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type docker-compose --file-path docker-compose.yml --docker-machine --machine-name dev1");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("dockerContainersFile", "docker-compose.yml");
        assertThat(project).hasArquillianConfig().withExtension("docker").withProperty("machineName", "dev1");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes() throws Exception {

        final FileResource<?> kubernetesResource = extractTestResource(project, "kubernetes.json");
        kubernetesResource.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/kubernetes.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("kubernetes");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes_with_resource_not_in_classpath() throws Exception {

        shell().execute("echo kubernetes > kubernetes.yml");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type kubernetes --file-path kubernetes.yml");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("kubernetes");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes_with_different_name_resource() throws Exception {

        final FileResource<?> kubernetesResource = extractTestResource(project, "kubernetes_1.json");
        kubernetesResource.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type kubernetes --file-path src/test/resources/kubernetes_1.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("kubernetes").withProperty("env.config.resource.name", "kubernetes_1.json");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes_with_url_resource() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type kubernetes --file-path http://foo.com");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("kubernetes").withProperty("env.config.url", "http://foo.com");
    }

    @Test
    public void should_setup_arquillian_cube_for_openshift() throws Exception {

        final FileResource<?> openshiftResource = extractTestResource(project, "openshift.json");
        openshiftResource.setContents("");

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type openshift --file-path src/test/resources/openshift.json");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-openshift").withType("pom").withScope("test");

        assertThat(project).hasArquillianConfig().withExtension("openshift").withProperty("definitionsFile", "src/test/resources/openshift.json");
    }
}
