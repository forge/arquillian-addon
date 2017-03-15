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
public class AddArquillianAlgeronPublisherTest extends ShellTestTemplate {

    @Test
    public void should_register_folder_publisher() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher folder --output-folder /tmp/pacts");

        assertThat(project).hasArquillianConfig().withExtension("algeron-consumer")
            .withProperty("publishConfiguration").containsExactly("provider: folder", "outputFolder: /tmp/pacts", "contractsFolder: target/pacts");
    }

    @Test
    public void should_register_url_publisher() throws TimeoutException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher url --url http://localhost");

        assertThat(project).hasArquillianConfig().withExtension("algeron-consumer")
            .withProperty("publishConfiguration").containsExactly("provider: url", "url: http://localhost", "contractsFolder: target/pacts");
    }

    @Test
    public void should_register_git_publisher() throws Exception {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact")
            .execute("arquillian-algeron-setup-publisher --publisher git --url http://localhost --comment newcomment");


        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-git-publisher").withType("pom").withScope("test");
        assertThat(project).hasArquillianConfig().withExtension("algeron-consumer")
            .withProperty("publishConfiguration").containsExactly("provider: git", "url: http://localhost", "comment: newcomment", "remote: origin", "contractsFolder: target/pacts");
    }

    @Test
    public void should_set_publish_contracts_property_as_environment_property_with_default_value_to_false() throws TimeoutException {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact");

        shell().execute("arquillian-algeron-setup-publisher --publisher folder --output-folder /tmp/pacts");

        assertThat(project).hasArquillianConfig().withExtension("algeron-consumer")
            .withProperty("publishContracts", "${env.publishcontracts:false}");
    }

}
