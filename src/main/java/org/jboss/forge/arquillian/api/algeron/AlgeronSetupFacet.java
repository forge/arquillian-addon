package org.jboss.forge.arquillian.api.algeron;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.api.AbstractVersionedFacet;
import org.jboss.forge.arquillian.api.ArquillianFacet;

@FacetConstraint(ArquillianFacet.class)
public abstract class AlgeronSetupFacet extends AbstractVersionedFacet {

    public static final String CONTRACT_TYPE = "contractType";

    public abstract DependencyBuilder createContractLibraryDependency();

    public abstract DependencyBuilder createAlgeronDependency();

    public abstract String getVersionPropertyName();

    public abstract String getContractType();

    @Override
    protected Coordinate getVersionedCoordinate() {
        return createContractLibraryDependency().getCoordinate();
    }

    @Override
    public boolean install() {
        if (getVersion() != null) {
            installDependencies();
            if (!isForgeConfigurationInstalled()) {
                configureForge();
            }
            return true;
        }
        return false;
    }

    private void configureForge() {
        // stores which kind of contract is been installed so it can be used by isInstalled method.
        // it is done in this way because AlgeronSetupFacet is recreated every time, so variables values are lost.

        addConfigurationProperty(CONTRACT_TYPE, getContractType());
    }

    private void addConfigurationProperty(String key, String value) {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();

        configuration.addProperty(key, value);
    }

    private void installDependencies() {
        final DependencyBuilder dependency = createAlgeronDependency();
        installContractLibrary(createContractLibraryDependency());
        installAlgeron(dependency);

        final String propertyName = getPropertyName(dependency);
        if (!propertyName.isEmpty()) {
            addConfigurationProperty(propertyName, "true");
        }
    }

    private String getPropertyName(DependencyBuilder dependencyBuilder) {
        String[] strings = dependencyBuilder.getCoordinate().getArtifactId().split("-");
        String type = "";
        if (strings.length > 3) {
            type = strings[3];
        }
        if (!type.isEmpty()) {
            return "is" + type.substring(0, 1).toUpperCase() + type.substring(1);
        }

        return type;
    }

    private void installContractLibrary(DependencyBuilder contractsDependency) {
        if (hasEffectiveDependency(contractsDependency)) {
            return;
        }

        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        final MetadataFacet metadataFacet = getFaceted().getFacet(MetadataFacet.class);

        metadataFacet.setDirectProperty(getVersionPropertyName(), getVersion());
        dependencyFacet.addDirectDependency(contractsDependency.setVersion(wrap(getVersionPropertyName())));
    }

    private void installAlgeron(DependencyBuilder algeronDependency) {
        if (hasEffectiveDependency(algeronDependency)) {
            return;
        }

        // version of Algeron is not required because it is provided by universe.
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        dependencyFacet.addDirectDependency(algeronDependency);

    }


    private boolean isForgeConfigurationInstalled() {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();

        final String contractType = getStringProperty(configuration.getProperty(CONTRACT_TYPE));
        return contractType != null && !contractType.isEmpty();
    }

    protected boolean isConsumerDependenciesInstalled() {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();
        final String contractType = getStringProperty(configuration.getProperty("isConsumer"));

        return contractType != null && !contractType.isEmpty();
    }

    protected boolean isProviderDependenciesInstalled() {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();
        final String contractType = getStringProperty(configuration.getProperty("isProvider"));

        return contractType != null && !contractType.isEmpty();
    }

    private String getStringProperty(Object property) {
        String propertyName = null;

        if (property instanceof String) {
            propertyName = (String) property;
        }

        return propertyName;
    }
    private boolean hasEffectiveDependency(DependencyBuilder frameworkDependency) {
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        return dependencyFacet.hasEffectiveDependency(frameworkDependency);
    }

    private String wrap(String versionPropertyName) {
        return "${" + versionPropertyName + "}";
    }
}
