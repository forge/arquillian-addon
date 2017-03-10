package org.jboss.forge.arquillian.api.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.util.YamlGenerator;

import java.util.Map;

@FacetConstraint(AlgeronConsumerFacet.class)
public class AlgeronPublisherFacet extends AbstractFacet<Project> implements ProjectFacet {

    public static final String PUBLISHER_EXTENSION_NAME = "algeron-consumer";

    private DependencyBuilder publisherDependency;
    private Map<String, String> configurationParameters;
    private String publishContracts;

    @Override
    public boolean install() {
        if (publisherDependency != null) {
            installPublisherDependency();
        }

        updateArquillianConfig();

        return isInstalled();
    }

    private void installPublisherDependency() {
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        dependencyFacet.addDirectDependency(publisherDependency);
    }

    @Override
    public boolean isInstalled() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        return arquillianFacet.getConfig().isExtensionRegistered(PUBLISHER_EXTENSION_NAME);
    }

    public void setConfigurationParameters(Map<String, String> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }

    public void setPublisherDependency(DependencyBuilder publisherDependency) {
        this.publisherDependency = publisherDependency;
    }

    public void setPublishContracts(String publishContracts) {
        this.publishContracts = publishContracts;
    }

    private void updateArquillianConfig() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        final ArquillianConfig config = arquillianFacet.getConfig();

        String publisherConfiguration = System.lineSeparator() + YamlGenerator.toYaml(configurationParameters);
        config.addExtensionProperty(PUBLISHER_EXTENSION_NAME, "publishConfiguration", publisherConfiguration);

        if (publishContracts != null) {
            config.addExtensionProperty(PUBLISHER_EXTENSION_NAME, "publishContracts", publishContracts);
        }

        arquillianFacet.setConfig(config);

    }

}
