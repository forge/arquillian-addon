package org.jboss.forge.arquillian.command.core;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.container.ContainerResolver;
import org.jboss.forge.arquillian.container.model.Container;
import org.jboss.forge.arquillian.container.model.ContainerType;
import org.jboss.forge.arquillian.container.model.Dependency;
import org.jboss.forge.arquillian.util.DependencyUtil;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@FacetConstraint(ArquillianFacet.class)
public class ContainerSetupWizard extends AbstractProjectCommand implements UIWizard {

    static final String CTX_CONTAINER = "arq-container";
    static final String CTX_CONTAINER_VERSION = "arq-container-version";
    static final String INSTALL_CONTAINER = "install-container";
    static final String CONTAINER_VERSION = "container-version";

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private ContainerResolver containerResolver;

    @Inject
    private DependencyResolver resolver;

    @Inject
    @WithAttributes(shortName = 'f', label = "Container Adapter Type", type = InputType.DROPDOWN)
    private UISelectOne<ContainerType> containerAdapterType;

    @Inject
    @WithAttributes(shortName = 'a', label = "Container Adapter", type = InputType.DROPDOWN, required = true)
    private UISelectOne<Container> containerAdapter;

    @Inject
    @WithAttributes(shortName = 'x', label = "Container Adapter Version", type = InputType.DROPDOWN)
    private UISelectOne<String> containerAdapterVersion;


    @Inject
    @WithAttributes(shortName = 'i', label = "Do you want Arquillian to install the container?", type = InputType.CHECKBOX)
    private UIInput<Boolean> installContainer;

    @Inject
    @WithAttributes(shortName = 'c', label = "Container Version", type = InputType.DROPDOWN)
    private UISelectOne<String> containerVersion;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Container Setup")
            .description("This addon will guide you through adding a Container Adapter for Arquillian");
    }

    @Override
    public void initializeUI(final UIBuilder builder) throws Exception {
        builder
            .add(containerAdapter)
            .add(containerAdapterVersion)
            .add(installContainer)
            .add(containerVersion);

        containerAdapterType.setValueChoices(Arrays.asList(ContainerType.values()));
        containerAdapterType.setEnabled(true);
        containerAdapter.setEnabled(true);
        containerAdapter.setValueChoices(() -> containerResolver.getContainers(containerAdapterType.getValue()));
        containerAdapter.setItemLabelConverter(source -> {
            if (source == null) {
                return null;
            }
            if (builder.getUIContext().getProvider().isGUI()) {
                return source.getName();
            }
            return source.getId();
        });
        containerAdapterVersion.setEnabled(() -> containerAdapter.hasValue());
        containerAdapterVersion.setValueChoices(() -> {
            if (containerAdapterVersion.isEnabled()) {
                return DependencyUtil.toVersionString(
                    resolver.resolveVersions(
                        DependencyQueryBuilder.create(
                            containerAdapter.getValue().asDependency().getCoordinate())), containerAdapter.getValue());

            }
            return Collections.emptyList();
        });
        containerAdapterVersion.setDefaultValue(() -> {
            if (containerAdapter.hasValue()) {
                return DependencyUtil.getLatestNonSnapshotVersionCoordinate(
                    resolver.resolveVersions(
                        DependencyQueryBuilder.create(
                            containerAdapter.getValue().asDependency().getCoordinate())), containerAdapter.getValue());
            }
            return null;
        });

        installContainer.setEnabled(() -> containerAdapter.hasValue() && containerAdapter.getValue().getDownload() != null && isDownloadCoordinateORUrlExists(containerAdapter.getValue().getDownload()));
        installContainer.setDefaultValue(() -> false);

        final Project project = getSelectedProject(builder);

        containerVersion.setEnabled(() -> containerAdapter.hasValue() && installContainer.hasValue() && isDownloadCoordinateExists(containerAdapter.getValue().getDownload()));
        containerVersion.setValueChoices(() -> {
            if (containerAdapter.hasValue() && containerVersion.isEnabled()) {
                return getAvailableVersions(containerAdapter.getValue(), project);
            }
            return Collections.emptyList();
        });
        containerVersion.setDefaultValue(() -> {
            if (containerAdapter.hasValue() && containerVersion.isEnabled()) {
               return DependencyUtil.getLatestNonSnapshotVersion(getAvailableVersions(containerAdapter.getValue(), project));
            }
            return null;
        });
    }

    private boolean isDownloadCoordinateORUrlExists(Dependency dependency) {
        return dependency.getUrl() != null || isDownloadCoordinateExists(dependency);
    }

    private boolean isDownloadCoordinateExists(Dependency dependency) {
        return dependency.getArtifactId() != null && dependency.getGroupId() != null;
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        return Results.success("Installed " + containerAdapter.getValue().getName());
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        Map<Object, Object> ctx = context.getUIContext().getAttributeMap();
        ctx.put(ContainerSetupWizard.CTX_CONTAINER, containerAdapter.getValue());
        ctx.put(ContainerSetupWizard.CTX_CONTAINER_VERSION, containerAdapterVersion.getValue());
        if (installContainer.isEnabled() && installContainer.hasValue()) {
            ctx.put(ContainerSetupWizard.INSTALL_CONTAINER, installContainer.getValue());
            ctx.put(ContainerSetupWizard.CONTAINER_VERSION, containerVersion.getValue());
        }
        return Results.navigateTo(AddContainerCommand.class);
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }


    private List<String> getAvailableVersions(Container container, Project project) {

        if (container != null) {
            final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

            final List<Coordinate> coordinates = dependencyFacet.resolveAvailableVersions(DependencyBuilder.create()
                .setGroupId(container.getDownload().getGroupId())
                .setArtifactId(container.getDownload().getArtifactId()));

            return DependencyUtil.toVersionString(coordinates);
        } else {
            return Collections.emptyList();
        }
    }

}
