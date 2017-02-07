package org.jboss.forge.arquillian.api.algeron;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.api.AbstractVersionedFacet;
import org.jboss.forge.arquillian.api.TestFrameworkFacet;

@FacetConstraint(TestFrameworkFacet.class)
@FacetConstraint(MetadataFacet.class)
@FacetConstraint(DependencyFacet.class)
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

        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();

        configuration.setProperty(CONTRACT_TYPE, getContractType());
    }

    private void installDependencies() {
        installContractLibrary(createContractLibraryDependency());
        installAlgeron(createAlgeronDependency());
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

    @Override
    public boolean isInstalled() {
        return hasEffectiveDependency(createAlgeronDependency()) && hasEffectiveDependency(createContractLibraryDependency());
    }

    public boolean isForgeConfigurationInstalled() {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();
        final String contractType = configuration.getString(CONTRACT_TYPE);
        return contractType != null && !contractType.isEmpty();
    }

    private boolean hasEffectiveDependency(DependencyBuilder frameworkDependency) {
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        return dependencyFacet.hasEffectiveDependency(frameworkDependency);
    }

    private String wrap(String versionPropertyName) {
        return "${" + versionPropertyName + "}";
    }
}
