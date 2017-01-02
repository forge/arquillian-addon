package test.integration;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.command.AddArquillianCommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddArquillianUniverseTest {

    @Inject
    private UITestHarness testHarness;

    @Inject
    private ProjectFactory factory;

    @Test
    public void shouldAddUniverseBOM() throws Exception {
        Project project = factory.createTempProject();
        
        try(CommandController addCommandController = testHarness.createCommandController(AddArquillianCommand.class, project.getRoot())) {
            addCommandController.initialize();
            addCommandController.setValueFor("arquillianVersion", "1.0.0.Alpha2");
            Result result = addCommandController.execute();           
            Assert.assertFalse(result instanceof Failed);
        }
        project = factory.findProject(project.getRoot());
        
        Assert.assertTrue(project.hasFacet(ArquillianFacet.class));
        
    }
}
