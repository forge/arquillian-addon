package test.integration.cube;


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.arquillian.api.ArquillianConfig;
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
        //create a file before run otherwise you will get error file not present.
        shell().execute("echo foo > src/test/resources/dockerFile");

        shell().execute("arquillian-setup --standalone --test-framework junit");

        shell().execute("arquillian-cube-setup --type docker --file src/test/resources/ --docker-file-name dockerFile");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("docker")).isTrue();

        final String fileConfig = arquillianConfig.getContentOfNode("extension@qualifier=docker/property@name=dockerContainersFile");
        assertThat(fileConfig).isNotNull();
        assertThat(fileConfig.trim()).contains("containerName:\n" +
                "\t\t  buildImage:\n" +
                "\t\t    dockerfileLocation: src/test/resources/\n" +
                "\t\t    noCache: true\n" +
                "\t\t    remove: true\n" +
                "\t\t    dockerfileName: dockerFile");

        final String defFormat = arquillianConfig.getContentOfNode("extension@qualifier=docker/property@name=definitionFormat");
        assertThat(defFormat).isNotNull();
        assertThat(defFormat).isEqualTo("CUBE");

    }

//    @Test
//    public void should_setup_arquillian_cube_for_docker_compose() throws Exception {
//
//        shell().execute("arquillian-setup --standalone --test-framework junit")
//            .execute("arquillian-cube-setup --type docker-compose");
//
//        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");
//    }
//
//
///*  Remove comment once new version of universe is released as 1.0.0.Alpha7 don't have cube kubernetes.
//    @Test
//    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes() throws Exception {
//
//        shell().execute("arquillian-setup --standalone --test-framework junit")
//            .execute("arquillian-cube-setup --type kubernetes");
//
//        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");
//        assertThat(project).hasConfiguration().withProperty("type", "kubernetes");
//    }*/
//
//    @Test
//    public void should_setup_arquillian_cube_for_openshift() throws Exception {
//
//        shell().execute("arquillian-setup --standalone --test-framework junit")
//            .execute("arquillian-cube-setup --type openshift --file src/test/resources/openshift.json");
//
//        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-openshift").withType("pom").withScope("test");
//    }
}
