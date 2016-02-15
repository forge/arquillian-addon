package test.integration;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.api.ArquillianExtensionFacet;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.command.AddArquillianCommand;
import org.jboss.forge.arquillian.command.AddArquillianExtensionCommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddArquillianExtensionTest {

    @Inject
    private UITestHarness testHarness;

    @Inject
    private ProjectFactory factory;

    @Inject
    private DependencyResolver resolver;

    @Test
    public void shouldAddExtension() throws Exception {
        
        Project project = factory.createTempProject();
        
        try(CommandController addCommandController = testHarness.createCommandController(AddArquillianCommand.class, project.getRoot())) {
            addCommandController.initialize();
            addCommandController.setValueFor("arquillianVersion", "1.0.0.Alpha2");
            Result result = addCommandController.execute();           
            Assert.assertFalse(result instanceof Failed);
        }
        project = factory.findProject(project.getRoot());
        
        try(CommandController addCommandController = testHarness.createCommandController(AddArquillianExtensionCommand.class, project.getRoot())) {
            addCommandController.initialize();
            addCommandController.setValueFor("arquillianExtension", "arquillian-cube-docker");
            Result result = addCommandController.execute();           
            Assert.assertFalse(result instanceof Failed);
        }

        System.out.println(project.getFacet(MavenFacet.class).getModelResource().getContents());
        
        Assert.assertTrue(project.hasFacet(ArquillianFacet.class));
        Assert.assertTrue(project.hasFacet(ArquillianExtensionFacet.class));
    }
}
