package org.jboss.forge.arquillian.command.cube;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.arquillian.model.cube.Target;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CubeCreateTestCommand extends AbstractProjectCommand implements UICommand {

    private static final Logger logger = Logger.getLogger(CubeCreateTestCommand.class.getName());

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    @WithAttributes(shortName = 'c', label = "Test Class", required = true)
    private UISelectOne<JavaClassSource> testClass;

    @Inject
    @WithAttributes(shortName = 'n', label = "Container Name", required = true)
    private UIInput<String> containerName;

    @Inject
    @WithAttributes(shortName = 'p', label = "Exposed Port", required = true)
    private UIInput<String> exposedPort;

    @Inject
    @WithAttributes(shortName = 's', label = "Service Name", required = true)
    private UIInput<String> serviceName;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(testClass);

        final Project project = getSelectedProject(builder);

        final List<JavaClassSource> sources = new ArrayList<>();
        project.getFacet(JavaSourceFacet.class).visitJavaTestSources(new JavaResourceVisitor() {
            @Override
            public void visit(VisitContext context, JavaResource javaResource) {
                JavaType<?> javaType;
                try {
                    javaType = javaResource.getJavaType();
                    if (javaType.isClass()) {
                        sources.add((JavaClassSource) javaType);
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "an exception was thrown", e);
                }
            }
        });

        this.testClass.setValueChoices(sources);
        this.testClass.setItemLabelConverter(JavaClassSource::getQualifiedName);

        if (isDocker(project)) {
            builder.add(containerName);
            builder.add(exposedPort);
        }

        if (isKubernetes(project) || isOpenshift(project)) {
            builder.add(serviceName);
        }
    }

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Cube"))
            .name("Arquillian Cube: Create Test")
            .description("This command creates skeleton for cube on given test class.");
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        final Project project = getSelectedProject(context);
        if (isStandalone(project)) {
            final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
            JavaClassSource updatedTest = this.testClass.getValue();

            CubeTestSetup cubeTestSetup = null;
            if (isKubernetes(project) || isOpenshift(project)) {
                cubeTestSetup = new KubernetesCubeTestSetup(serviceName.getValue());
            } else if (isDocker(project)) {
                cubeTestSetup = new DockerCubeTestSetup(containerName.getValue(), exposedPort.getValue());
            }
            if (cubeTestSetup == null) {
                return Results.fail("Could not find arquillian-cube-docker OR arquillian-cube-kubernetes OR arquillian-cube-openshift dependency in pom.xml. Please install it using `arquillian-cube-setup` command");
            }

            cubeTestSetup.updateTest(updatedTest);
            java.saveTestJavaSource(updatedTest);

            return Results.success("Test set up for cube has been done successfully.");
        } else {
            return Results.fail("No standalone dependency present inside pom.xml");
        }
    }

    private boolean isDocker(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        return dependencyFacet.hasEffectiveDependency(Target.DOCKER.getDependencyBuilder());

    }

    private boolean isKubernetes(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        return dependencyFacet.hasEffectiveDependency(Target.KUBERNETES.getDependencyBuilder());
    }

    private boolean isOpenshift(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        return dependencyFacet.hasEffectiveDependency(Target.OPENSHIFT.getDependencyBuilder());
    }

    private boolean isStandalone(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        final DependencyBuilder junit = DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-junit-standalone")
            .setScopeType("test")
            .setPackaging("pom");

        final DependencyBuilder testng = DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-testng-standalone")
            .setScopeType("test")
            .setPackaging("pom");
        return dependencyFacet.hasEffectiveDependency(junit) || dependencyFacet.hasEffectiveDependency(testng);
    }


    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasFacet(CubeSetupFacet.class);
        }
        return parent;
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

}
