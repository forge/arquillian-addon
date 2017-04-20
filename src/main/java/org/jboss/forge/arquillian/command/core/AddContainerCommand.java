package org.jboss.forge.arquillian.command.core;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.container.ContainerInstaller;
import org.jboss.forge.arquillian.container.DependencyManager;
import org.jboss.forge.arquillian.container.ProfileManager;
import org.jboss.forge.arquillian.container.model.Container;
import org.jboss.forge.arquillian.container.model.Dependency;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;
import org.jboss.forge.arquillian.util.DependencyUtil;

public class AddContainerCommand extends AbstractProjectCommand implements UIWizardStep {

    public static final Logger logger = Logger.getLogger(AddContainerCommand.class.getName());

    private final Map<Dependency, InputComponent<?, String>> dependencyVersions = new HashMap<>();

    @Inject
    private InputComponentFactory inputFactory;

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private ContainerInstaller containerInstaller;

    @Inject
    private ProfileManager profileManager;

    @Inject
    private ResourceFactory resourceFactory;
    @Inject
    private DependencyManager dependencyManager;

    @Inject
    private DependencyResolver resolver;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Add Container")
            .description("This addon will help you setup a Arquillian Container Adapter");
    }

    @Override
    public void initializeUI(final UIBuilder builder) throws Exception {
        Container selectedContainer =
            (Container) builder.getUIContext().getAttributeMap().get(ContainerSetupWizard.CTX_CONTAINER);
        if (selectedContainer == null || selectedContainer.getDependencies() == null) {
            return;
        }
        for (final Dependency dependency : selectedContainer.getDependencies()) {
            UISelectOne<String> dependencyVersion =
                inputFactory.createSelectOne(dependency.getArtifactId() + "-version", String.class);
            builder.add(dependencyVersion);
            dependencyVersions.put(dependency, dependencyVersion);

            final DependencyQuery dependencyCoordinate = DependencyQueryBuilder.create(
                DependencyBuilder.create()
                    .setGroupId(dependency.getGroupId())
                    .setArtifactId(dependency.getArtifactId())
                    .getCoordinate());

            dependencyVersion.setEnabled(true);
            dependencyVersion.setValueChoices(() -> DependencyUtil.toVersionString(
                resolver.resolveVersions(dependencyCoordinate)));
            dependencyVersion.setDefaultValue(() -> DependencyUtil.getLatestNonSnapshotVersionCoordinate(
                resolver.resolveVersions(dependencyCoordinate)));
        }
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        Map<Object, Object> ctx = context.getUIContext().getAttributeMap();
        Container container = (Container) ctx.get(ContainerSetupWizard.CTX_CONTAINER);
        String version = (String) ctx.get(ContainerSetupWizard.CTX_CONTAINER_VERSION);

        Project project = getSelectedProject(context);
        ArquillianFacet arquillian = project.getFacet(ArquillianFacet.class);
        ArquillianConfig config = arquillian.getConfig();
        final String profileId = container.getProfileId();
        final boolean supportedByChameleon = container.isSupportedByChameleon(version);
        if (supportedByChameleon) {
            dependencyManager.addChameleonDependency(project);
            if (config.containsDefaultContainer()) {
                config.addContainer(profileId);
            } else {
                config.addContainerWithAttribute(profileId, "default", "true");
            }

            config.addContainerProperty(profileId, "chameleonTarget", "${chameleon.target}");
            if ("Arquillian Container Tomcat Managed".equals(container.getName())) {
                addTomcatConfiguration(project, config, version, profileId);
            }
        } else {
            config.addContainer(profileId);
        }

        arquillian.setConfig(config);

        containerInstaller.installContainer(
            project,
            container,
            version,
            getVersionedDependenciesMap());

        final Object containerInstall = ctx.get(ContainerSetupWizard.INSTALL_CONTAINER);

        if (containerInstall != null) {
            boolean installContainer = (Boolean) containerInstall;
            String containerVersion = (String) ctx.get(ContainerSetupWizard.CONTAINER_VERSION);

            if (installContainer) {
                profileManager.addContainerConfiguration(container, project, containerVersion);
            }
        }

        return Results.success("Installed " + container.getName() + " dependencies");
    }

    private void addTomcatConfiguration(Project project, ArquillianConfig config, String version, String profileId) {

        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(version);

        if (matcher.find()) {
            final String versionPrefix = matcher.group(0);
            final String resourceName = "tomcat" + versionPrefix + "-server.xml";

            setResourcesContentForTomcat(project, resourceName);
            setResourcesContentForTomcat(project, "tomcat-users.xml");

            config.addContainerProperty(profileId, "user", "arquillian");
            config.addContainerProperty(profileId, "pass", "arquillian");
            config.addContainerProperty(profileId, "serverConfig",
                "../../../../../src/test/resources/" + resourceName);
        }
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasFacet(ArquillianFacet.class);
        }
        return parent;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

    private Map<Dependency, String> getVersionedDependenciesMap() {
        if (dependencyVersions.isEmpty()) {
            return null;
        }
        Map<Dependency, String> resolved = new HashMap<>();
        for (Map.Entry<Dependency, InputComponent<?, String>> dep : dependencyVersions.entrySet()) {
            resolved.put(dep.getKey(), (String) dep.getValue().getValue());
        }
        return resolved;
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        return null;
    }

    private void setResourcesContentForTomcat(Project project, String resourceName) {
        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        final FileResource<?> testResource = facet.getTestResource(resourceName);

        final Resource<URL> resource = resourceFactory.create(getClass().getClassLoader().getResource(resourceName));
        if (resource.exists()) {
            testResource.setContents(resource.getContents());
        } else {
            logger.severe("resource with name: " + resourceName + " does not exists in classpath.");
        }
    }
}
