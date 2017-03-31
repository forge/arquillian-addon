package test.integration.algeron;

import org.arquillian.algeron.pact.provider.spi.Provider;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddDependencies({"org.arquillian.algeron:arquillian-algeron-pact-provider-spi", "au.com.dius:pact-jvm-consumer_2.11"})
@AddPackage(containing = ShellTestTemplate.class)
public class AddArquillianAlgeronCreateProviderTest extends ShellTestTemplate {


    @Test
    public void should_create_provider_test() throws TimeoutException, FileNotFoundException {
        shell().execute("arquillian-setup --standalone --test-framework junit")
            .execute("arquillian-algeron-setup-provider --contracts-library pact");

        shell().execute("arquillian-create-test --named MyContractTest --target-package org.superbiz")
            .execute("arquillian-algeron-add-provider-test --provider myprovider --test-class org.superbiz.MyContractTest");

        final JavaClassSource testClass = extractClass(project, "org.superbiz.MyContractTest");

        assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");
        assertThat(testClass).hasAnnotation(Provider.class).withValue("myprovider");

        assertThat(testClass).hasMethod("should_verify_contract");
        assertThat(testClass).hasField("target").annotatedWith(ArquillianResource.class);

    }
}
