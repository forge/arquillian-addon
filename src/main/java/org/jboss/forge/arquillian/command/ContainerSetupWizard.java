package org.jboss.forge.arquillian.command;

import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.api.ContainerInstallEvent;
import org.jboss.forge.arquillian.container.ContainerResolver;
import org.jboss.forge.arquillian.container.model.Container;
import org.jboss.forge.arquillian.container.model.ContainerType;
import org.jboss.forge.arquillian.util.DependencyUtil;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@FacetConstraint(ArquillianFacet.class)
public class ContainerSetupWizard extends AbstractProjectCommand implements UIWizard {

    static final String CTX_CONTAINER = "arq-container";
    static final String CTX_CONTAINER_VERSION = "arq-container-version";

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private ContainerResolver containerResolver;

    @Inject
    private DependencyResolver resolver;

    @Inject
    @Any
    private Event<ContainerInstallEvent> installEvent;

    @Inject
    @WithAttributes(shortName = 'f', label = "Container Adapter Type", type = InputType.DROPDOWN, required = false)
    private UISelectOne<ContainerType> containerAdapterType;

    @Inject
    @WithAttributes(shortName = 'c', label = "Container Adapter", type = InputType.DROPDOWN, required = true)
    private UISelectOne<Container> containerAdapter;

    @Inject
    @WithAttributes(shortName = 'x', label = "Container Adapter Version", type = InputType.DROPDOWN)
    private UISelectOne<String> containerAdapterVersion;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Container Setup")
            .description("This addon will guide you through adding a Container Adapter for Arquillian");
    }

    @Override
    public void initializeUI(final UIBuilder builder) throws Exception {
        builder// .add(containerAdapterType)
            .add(containerAdapter)
            .add(containerAdapterVersion);

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

}
