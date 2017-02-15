package test.integration.cube;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(containing = ShellTestTemplate.class)
public class AddCubeDependenciesCommandTest extends ShellTestTemplate {

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_docker() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type docker");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");
        assertThat(project).hasConfiguration().withProperty("type", "docker");
    }

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_docker_compose() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type docker-compose");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-docker").withType("pom").withScope("test");
        assertThat(project).hasConfiguration().withProperty("type", "docker-compose");
    }


/*  Remove comment once new version of universe is released as 1.0.0.Alpha7 don't have cube kubernetes.
    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_kubernetes() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type kubernetes");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-kubernetes").withType("pom").withScope("test");
        assertThat(project).hasConfiguration().withProperty("type", "kubernetes");
    }*/

    @Test
    public void should_add_arquillian_cube_dependencies_and_property_for_openshift() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-cube-setup --type openshift");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-cube-openshift").withType("pom").withScope("test");
        assertThat(project).hasConfiguration().withProperty("type", "openshift");
    }
}
