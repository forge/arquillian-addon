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
public class AddArquillianAlgeronRetrieverTest extends ShellTestTemplate {

    @Test
    public void should_register_folder_retriever() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever folder --contract-folder /tmp");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-provider")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-provider/property@name=retrieverConfiguration");
        assertThat(configuration).isNotNull();
        assertThat(configuration)
            .contains("provider: folder", "contractsFolder: /tmp");
    }

    @Test
    public void should_register_git_retriever() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever git --url http://localhost");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-provider")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-provider/property@name=retrieverConfiguration");
        assertThat(configuration).isNotNull();

        assertThat(configuration)
            .contains("provider: git", "url: http://localhost");

        ForgeAssertions.assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-git-retriever").withType("pom").withScope("test");

    }

    @Test
    public void should_register_maven_retriever() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever maven --maven-coordinates org.superbiz:foo:1.0.0");

        final FileResource<?> arquillianXml = extractTestResource(project, "arquillian.xml");
        assertThat(arquillianXml.exists()).isTrue();

        final ArquillianConfig arquillianConfig = new ArquillianConfig(arquillianXml.getResourceInputStream());
        assertThat(arquillianConfig.isExtensionRegistered("algeron-provider")).isTrue();
        final String configuration = arquillianConfig.getContentOfNode("extension@qualifier=algeron-provider/property@name=retrieverConfiguration");
        assertThat(configuration).isNotNull();

        assertThat(configuration)
            .contains("provider: maven", "coordinates: org.superbiz:foo:1.0.0");

        ForgeAssertions.assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-maven-retriever").withType("pom").withScope("test");

    }

}
