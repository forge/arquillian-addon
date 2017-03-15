package test.integration.algeron;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import java.util.concurrent.TimeoutException;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddPackage(containing = ShellTestTemplate.class)
public class AddArquillianAlgeronRetrieverTest extends ShellTestTemplate {

    @Test
    public void should_register_folder_retriever() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever folder --contract-folder /tmp");

        assertThat(project).hasArquillianConfig().withExtension("algeron-provider")
            .withProperty("retrieverConfiguration").containsExactly("provider: folder", "contractsFolder: /tmp");
    }

    @Test
    public void should_register_git_retriever() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever git --url http://localhost");

        assertThat(project).hasArquillianConfig().withExtension("algeron-provider")
            .withProperty("retrieverConfiguration").containsExactly("provider: git", "url: http://localhost");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-git-retriever").withType("pom").withScope("test");
    }

    @Test
    public void should_register_maven_retriever() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-retriever --retriever maven --maven-coordinates org.superbiz:foo:1.0.0");

        assertThat(project).hasArquillianConfig().withExtension("algeron-provider")
            .withProperty("retrieverConfiguration").containsExactly("provider: maven", "coordinates: org.superbiz:foo:1.0.0");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-maven-retriever").withType("pom").withScope("test");
    }

}
