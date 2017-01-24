package test.integration;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.command.AddAlgeronConsumerDependenciesCommand;
import org.jboss.forge.arquillian.command.AddArquillianCommand;
import org.jboss.forge.arquillian.command.AddTestFrameworkCommand;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;
import org.jboss.forge.arquillian.testframework.algeron.AlgeronConsumer;
import org.jboss.forge.arquillian.testframework.junit.JUnitFacet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class AddArquillianAlgeronConsumerTest
{

   @Inject
   private UITestHarness testHarness;

   @Inject
   private ProjectFactory factory;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void should_add_arquillian_algeron_consumer_dependencies() throws Exception {

      Project project = factory.createTempProject();

      try(CommandController addCommandController = testHarness.createCommandController(AddArquillianCommand.class, project.getRoot())) {
         addCommandController.initialize();
         addCommandController.setValueFor("arquillianVersion", "1.0.0.Alpha6");
         Result result = addCommandController.execute();

         checkResult(result);
      }
      project = factory.findProject(project.getRoot());

      try(CommandController addTestFrameworkCommandController = testHarness.createCommandController(AddTestFrameworkCommand.class, project.getRoot())) {
         addTestFrameworkCommandController.initialize();
         addTestFrameworkCommandController.setValueFor("standalone", true);
         addTestFrameworkCommandController.setValueFor("testFramework", "junit");

         Result result = addTestFrameworkCommandController.execute();

         checkResult(result);

      }

      facetFactory.install(project, JUnitFacet.class);
      try(CommandController addAlgeronConsumerCommandController = testHarness.createCommandController(AddAlgeronConsumerDependenciesCommand.class, project.getRoot())) {
         addAlgeronConsumerCommandController.initialize();
         addAlgeronConsumerCommandController.setValueFor("type", "pact");
         Result result = addAlgeronConsumerCommandController.execute();

         checkResult(result);

         Assert.assertTrue(project.hasFacet(AlgeronConsumer.class));

         final DependencyFacet facet = project.getFacet(DependencyFacet.class);
         Assert.assertTrue(facet.hasDirectDependency(ContractConsumerLibrary.PACT.getAlgeronConsumer()));
         Assert.assertTrue(facet.hasDirectDependency(ContractConsumerLibrary.PACT.getContractConsumer()));

      }

   }

   private void checkResult(Result result) {
      printIfFailed(result);
      Assert.assertFalse(result instanceof Failed);
   }

   public void printIfFailed(Result result)
   {
      if (result instanceof  Failed)
      {
         Failed failed = (Failed) result;
         System.out.println(failed);
      }
   }

}