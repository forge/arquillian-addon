package test.integration.algeron;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddPackage(containing = ShellTestTemplate.class)
public class AddArquillianAlgeronProviderTest extends ShellTestTemplate {

    @Test
    public void should_add_arquillian_algeron_provider_dependencies() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-pact-provider").withType("pom").withScope("test");
        assertThat(project).hasDirectDependency("au.com.dius:pact-jvm-provider_2.11").withScope("test");
        assertThat(project).hasConfiguration().withProperties("isProvider:true", "contractType:PACT");
    }

    @Test
    public void should_add_arquillian_algeron_provider_and_consumer_dependencies() throws Exception {

        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact")
            .execute("arquillian-algeron-setup-consumer --contracts-library pact");

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-pact-consumer").withType("pom").withScope("test");
        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-algeron-pact-provider").withType("pom").withScope("test");
        assertThat(project).hasDirectDependency("au.com.dius:pact-jvm-consumer_2.11").withScope("test");
        assertThat(project).hasDirectDependency("au.com.dius:pact-jvm-provider_2.11").withScope("test");
        assertThat(project).hasConfiguration().withProperties("isProvider:true", "isConsumer:true", "contractType:PACT");

    }

}
