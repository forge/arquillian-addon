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
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.arquillian.container.model.Target;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CubeCreateTestCommand extends AbstractProjectCommand implements UICommand {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    @WithAttributes(shortName = 'c', label = "Test Class", required = true)
    private UISelectOne<JavaClassSource> testClass;

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
                    // Do nothing
                }
            }
        });

        this.testClass.setValueChoices(sources);
        this.testClass.setItemLabelConverter(JavaClassSource::getQualifiedName);
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
            if (isKubernetes(project)) {
                cubeTestSetup = new KubernetesCubeTestSetup();
            } else if (isDocker(project)) {
                cubeTestSetup = new DockerCubeTestSetup();
            } else if (isOpenshift(project)) {
                cubeTestSetup = new OpenshiftCubeTestSetup();
            }
            if (cubeTestSetup == null) {
                return Results.fail("Could not find arquillian-cube-docker OR arquillian-cube-kuberneters OR arquilliaa-cube-openshift dependency in pom.xml. Please install it using `arquillian-cube-setup` command");
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

    public boolean isOpenshift(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

        return dependencyFacet.hasEffectiveDependency(Target.OPENSHIFT.getDependencyBuilder());
    }
}
