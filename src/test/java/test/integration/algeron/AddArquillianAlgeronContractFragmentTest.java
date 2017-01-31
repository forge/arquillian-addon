package test.integration.algeron;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import org.arquillian.algeron.pact.consumer.spi.Pact;
import org.arquillian.algeron.pact.consumer.spi.PactVerification;
import org.jboss.arquillian.junit.Arquillian;
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
@AddDependencies({"org.assertj:assertj-core", "org.arquillian.algeron:arquillian-algeron-pact-consumer-spi", "au.com.dius:pact-jvm-consumer_2.11"})
@AddPackage(containing = ShellTestTemplate.class)
public class AddArquillianAlgeronContractFragmentTest extends ShellTestTemplate
{
   @Test
   public void should_create_contract_fragment() throws TimeoutException, FileNotFoundException {
      shell().execute("arquillian-setup --standalone --test-framework junit")
              .execute("arquillian-algeron-setup-consumer --contracts-library pact");

      shell().execute("arquillian-create-test --named MyContractTest --target-package org.superbiz")
              .execute("arquillian-algeron-create-contract-fragment --consumer myconsumer --provider myprovider --fragment myFragment --test-class org.superbiz.MyContractTest");

      final JavaClassSource testClass = extractClass(project, "org.superbiz.MyContractTest");

      assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");
      assertThat(testClass).hasAnnotation(Pact.class).withEntry("consumer", "myconsumer")
              .withEntry("provider", "myprovider");

      assertThat(testClass).hasMethod("myFragment", PactDslWithProvider.class);
      assertThat(testClass).hasMethod("should_my_Fragment_between_myconsumer_and_myprovider")
              .withAnnotation(PactVerification.class).withEntry("fragment", "myFragment");

   }

}
