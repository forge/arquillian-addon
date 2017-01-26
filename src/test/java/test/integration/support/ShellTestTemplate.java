package test.integration.support;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.After;
import org.junit.Before;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public abstract class ShellTestTemplate {

    @ArquillianResource
    URL url;

    public static final String PACKAGE_NAME = "test.integration.support";
    private ShellTest shellTest;
    protected Project project;
    private ProjectFactory projectFactory;

    @Before
    public void setUp() throws Exception {
        final AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
        projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
        shellTest = addonRegistry.getServices(ShellTest.class).get();
        project = projectFactory.createTempProject(asList(ArquillianFacet.class, JavaSourceFacet.class));
        shellTest.getShell().setCurrentResource(project.getRoot());
    }

    @After
    public void tearDown() throws Exception {
        if (shellTest != null) {
            shellTest.close();
        }
        if (projectFactory != null) {
            projectFactory.invalidateCaches();
        }

    }

    protected ShellExecutor shell() {
        return new ShellExecutor(shellTest, 30, TimeUnit.SECONDS);
    }

    protected JavaClassSource extractClass(Project project, String className) throws java.io.FileNotFoundException {
        return (JavaClassSource) project.getFacet(JavaSourceFacet.class)
                .getTestJavaResource(className)
                .getJavaType();
    }

    protected FileResource<?> extractTestResource(Project project, String filename)
    {
        ResourcesFacet resources = project.getFacet(ResourcesFacet.class);
        return resources.getTestResource(filename);
    }

}
