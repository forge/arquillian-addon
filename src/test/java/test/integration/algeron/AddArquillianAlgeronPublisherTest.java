package test.integration.algeron;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.arquillian.api.ArquillianConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;
import test.integration.support.assertions.ForgeAssertions;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(containing = ShellTestTemplate.class)
public class AddArquillianAlgeronPublisherTest extends ShellTestTemplate {

    @Test
    public void should_register_folder_publisher() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher folder --output-folder /tmp/pacts");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-consumer")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-consumer/property@name=publishConfiguration");
        assertThat(configuration).isNotNull();
        assertThat(configuration)
            .contains("provider: folder", "outputFolder: /tmp/pacts");
    }

    @Test
    public void should_register_url_publisher() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher url --url http://localhost");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-consumer")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-consumer/property@name=publishConfiguration");
        assertThat(configuration).isNotNull();

        assertThat(configuration)
            .contains("provider: url", "url: http://localhost");
    }

    @Test
    public void should_register_git_publisher() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher git --url http://localhost --comment newcomment");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-consumer")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-consumer/property@name=publishConfiguration");
        assertThat(configuration).isNotNull();

        assertThat(configuration)
            .contains("provider: git", "url: http://localhost", "comment: newcomment");

        ForgeAssertions.assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-git-publisher").withType("pom").withScope("test");

    }

    @Test
    public void should_set_publish_contracts_property_as_environment_property_with_default_value_to_false() throws TimeoutException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact");

        shell().execute("arquillian-algeron-setup-publisher --publisher folder --output-folder /tmp/pacts");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-consumer")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-consumer/property@name=publishContracts");
        assertThat(configuration).isNotNull();

        assertThat(configuration)
            .contains("env.publishcontracts:false");
    }

}
