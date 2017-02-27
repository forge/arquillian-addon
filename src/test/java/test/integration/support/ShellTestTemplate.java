package test.integration.support;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.test.ShellTest;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

public abstract class ShellTestTemplate {

    public static final String PACKAGE_NAME = "test.integration.support";

    protected Project project;

    @ArquillianResource
    private URL url;

    @Inject
    private ShellTest shellTest;

    @Inject
    protected ProjectFactory projectFactory;

    @Before
    public void setUp() throws Exception {
        shellTest.getShell().setCurrentResource(project.getRoot());
    }

    @After
    public void tearDown() throws Exception {
        shellTest.close();
        projectFactory.invalidateCaches();
    }

    protected ShellExecutor shell() {
        return new ShellExecutor(shellTest, 30, TimeUnit.SECONDS);
    }

    protected JavaClassSource extractClass(Project project, String className) throws java.io.FileNotFoundException {
        return (JavaClassSource) project.getFacet(JavaSourceFacet.class)
            .getTestJavaResource(className)
            .getJavaType();
    }

    protected FileResource<?> extractTestResource(Project project, String filename) {
        ResourcesFacet resources = project.getFacet(ResourcesFacet.class);
        return resources.getTestResource(filename);
    }

}
