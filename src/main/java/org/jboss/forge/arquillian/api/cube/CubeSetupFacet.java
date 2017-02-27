package org.jboss.forge.arquillian.api.cube;


import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.api.AbstractVersionedFacet;
import org.jboss.forge.arquillian.api.ArquillianConfig;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.container.model.CubeConfiguration;

import java.util.Arrays;
import java.util.Map;

@FacetConstraint(ArquillianFacet.class)
public class CubeSetupFacet extends AbstractVersionedFacet {

    private Map<String, String> configurationParameters;

    private CubeConfiguration cubeConfiguration;

    public CubeConfiguration getCubeConfiguration() {
        return cubeConfiguration;
    }

    public void setCubeConfiguration(CubeConfiguration cubeConfiguration) {
        this.cubeConfiguration = cubeConfiguration;
    }

    public void setConfigurationParameters(Map<String, String> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }

    @Override
    protected Coordinate getVersionedCoordinate() {
        return cubeConfiguration.getDependency().getCoordinate();
    }

    @Override
    public boolean install() {
        installDependencies();
        updateArquillianConfiguration();

        return true;
    }

    private void updateArquillianConfiguration() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        final ArquillianConfig config = arquillianFacet.getConfig();

        config.addExtensionProperty(cubeConfiguration.getQualifierForExtension(), configurationParameters);

        arquillianFacet.setConfig(config);
    }

    private void installDependencies() {
        if (hasEffectiveDependency(cubeConfiguration.getDependency())) {
            return;
        }

        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        dependencyFacet.addDirectDependency(cubeConfiguration.getDependency());
    }

    @Override
    public boolean isInstalled() {
       return Arrays.stream(CubeConfiguration.values())
            .map(CubeConfiguration::getDependency)
            .anyMatch(this::hasEffectiveDependency);
    }

}
