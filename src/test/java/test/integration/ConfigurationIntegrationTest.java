package test.integration;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;
import test.integration.support.assertions.ForgeAssertions;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class ConfigurationIntegrationTest extends ShellTestTemplate {


    @Test
    public void should_configure_container() throws Exception {
        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        shell().execute("arquillian-setup --container-adapter tomcat-embedded-6 --test-framework junit");

        assertThat(arquillianXML.getContents()).contains("<property name=\"bindHttpPort\">9090</property>");
    }

    @Test
    public void should_configure_container_with_chameleon_if_chameleon_supported() throws Exception {

        shell().execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit");

        ForgeAssertions.assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-chameleon").withType("pom").withScope("test");

        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        assertThat(arquillianXML.getContents()).contains("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:REMOTE</property>");

    }

    @Test
    public void should_override_chameleon_target_if_chameleon_supported() throws Exception {

        shell().execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit");

        ForgeAssertions.assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-chameleon").withType("pom").withScope("test");

        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        assertThat(arquillianXML.getContents()).contains("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:REMOTE</property>");

        shell().execute("arquillian-container-setup --container-adapter wildfly-managed");
        assertThat(arquillianXML.getContents()).contains("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:MANAGED</property>");
    }


    @Test
    public void should_override_configuration_options() throws Exception {

        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        shell().execute("arquillian-setup --container-adapter tomcat-embedded-6 --test-framework junit");

        shell().execute("arquillian-container-configuration --container arquillian-tomcat-embedded-6 --container-option bindHttpPort");
        assertThat(arquillianXML.getContents()).contains("<property name=\"bindHttpPort\">9090</property>");

        shell().execute("arquillian-container-configuration --container arquillian-tomcat-embedded-6 --container-option bindHttpPort --container-value 8081");

        assertThat(arquillianXML.getContents()).contains("<property name=\"bindHttpPort\">8081</property>");
    }

    @Test
    public void should_create_arquillian_xml_on_setup() throws Exception {

        shell().execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit");

        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        assertThat(arquillianXML).isNotNull();
        assertThat(arquillianXML.exists()).isTrue();
    }

}
