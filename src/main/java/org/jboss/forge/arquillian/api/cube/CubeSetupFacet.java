package org.jboss.forge.arquillian.api.cube;


import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.arquillian.api.core.AbstractVersionedFacet;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.model.cube.CubeConfiguration;

import java.util.Arrays;
import java.util.Map;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

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

        return isInstalled();
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

    public boolean isDocker(UISelectOne<String> type) {
        return type.hasValue() && "docker".equals(getStringForCLIDisplay(type.getValue()));
    }

    public boolean isDockerOrDockerCompose(UISelectOne<String> type) {
        return isDocker(type) || isDockerCompose(type);
    }


    public boolean isKubernetes(UISelectOne<String> type) {
        return type.hasValue() && "kubernetes".equals(getStringForCLIDisplay(type.getValue()));
    }

    private boolean isDockerCompose(UISelectOne<String> type) {
        return type.hasValue() && "docker-compose".equals(getStringForCLIDisplay(type.getValue()));
    }

    private boolean isOpenshift(UISelectOne<String> type) {
        return type.hasValue() && "openshift".equals(getStringForCLIDisplay(type.getValue()));
    }


    public void setCubeConfiguration(UISelectOne<String> type) {
        if (isKubernetes(type)) {
            this.setCubeConfiguration(CubeConfiguration.KUBERNETES);
        } else if (isDocker(type)) {
            this.setCubeConfiguration(CubeConfiguration.DOCKER);
        } else if (isDockerCompose(type)) {
            this.setCubeConfiguration(CubeConfiguration.DOCKER_COMPOSE);
        } else if (isOpenshift(type)) {
            this.setCubeConfiguration(CubeConfiguration.OPENSHIFT);
        }

    }
}
