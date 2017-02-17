package org.jboss.forge.arquillian.api.cube;


import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.api.AbstractVersionedFacet;
import org.jboss.forge.arquillian.api.ArquillianConfig;
import org.jboss.forge.arquillian.api.ArquillianFacet;

import java.util.Map;

@FacetConstraint(ArquillianFacet.class)
public abstract class CubeSetupFacet extends AbstractVersionedFacet {

    private Map<String, String> configurationParameters;

    public abstract DependencyBuilder createCubeDependency();

    public abstract String getQualifierForExtension();

    public abstract String getType();

    public abstract String getKeyForFileLocation();

    @Override
    protected Coordinate getVersionedCoordinate() {
        return createCubeDependency().getCoordinate();
    }

    @Override
    public boolean install() {
        installDependencies();
        updateArquillianConfiguration();

        return true;
    }

    public void setConfigurationParameters(Map<String, String> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }

    private void updateArquillianConfiguration() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        final ArquillianConfig config = arquillianFacet.getConfig();

        config.addExtensionProperty(getQualifierForExtension(), configurationParameters);

        arquillianFacet.setConfig(config);
    }

    private void installDependencies() {
        if (hasEffectiveDependency(createCubeDependency())) {
            return;
        }

        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        dependencyFacet.addDirectDependency(createCubeDependency());
    }

    @Override
    public boolean isInstalled() {
        return hasEffectiveDependency(createCubeDependency());
    }

}
