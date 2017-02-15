package org.jboss.forge.arquillian.api.cube;


import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.api.AbstractVersionedFacet;
import org.jboss.forge.arquillian.api.ArquillianFacet;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

@FacetConstraint(ArquillianFacet.class)
public abstract class CubeSetupFacet extends AbstractVersionedFacet {

    private static final String TYPE = "type";

    public abstract DependencyBuilder createCubeDependency();

    public abstract String getType();

    @Override
    protected Coordinate getVersionedCoordinate() {
        return createCubeDependency().getCoordinate();
    }

    @Override
    public boolean install() {
        installDependencies();
        setType(TYPE, getType());
        return true;
    }

    private void setType(String key, String value) {
        final ConfigurationFacet configurationFacet = getFaceted().getFacet(ConfigurationFacet.class);
        final Configuration configuration = configurationFacet.getConfiguration();
        final String property = getStringForCLIDisplay(value);

        configuration.setProperty(key, property);
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
        return hasEffectiveDependency(createCubeDependency()) && isCubeType();
    }

    private boolean isCubeType() {
        final ConfigurationFacet facet = getFaceted().getFacet(ConfigurationFacet.class);
        final String type = (String) facet.getConfiguration().getProperty(TYPE);

        return type != null;
    }
}
