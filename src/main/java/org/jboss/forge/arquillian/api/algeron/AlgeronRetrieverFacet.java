package org.jboss.forge.arquillian.api.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.api.ArquillianConfig;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.api.YamlGenerator;

import java.util.Map;

@FacetConstraint(AlgeronProvider.class)
public class AlgeronRetrieverFacet extends AbstractFacet<Project> implements ProjectFacet {

    public static final String RETRIEVER_EXTENSION_NAME = "algeron-provider";

    private DependencyBuilder retrieverDependency;
    private Map<String, String> configurationParameters;

    @Override
    public boolean install() {
        if (retrieverDependency != null) {
            installRetrieverDependeny();
        }

        updateArquillianConfig();

        return isInstalled();
    }

    @Override
    public boolean isInstalled() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        return arquillianFacet.getConfig().isExtensionRegistered(RETRIEVER_EXTENSION_NAME);
    }

    public void setConfigurationParameters(Map<String, String> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }

    public void setRetrieverDependency(DependencyBuilder retrieverDependency) {
        this.retrieverDependency = retrieverDependency;
    }

    private void installRetrieverDependeny() {
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        dependencyFacet.addDirectDependency(retrieverDependency);
    }

    private void updateArquillianConfig() {
        final ArquillianFacet arquillianFacet = getFaceted().getFacet(ArquillianFacet.class);
        final ArquillianConfig config = arquillianFacet.getConfig();

        String publisherConfiguration = System.lineSeparator() + YamlGenerator.toYaml(configurationParameters);
        config.addExtensionProperty(RETRIEVER_EXTENSION_NAME, "retrieverConfiguration", publisherConfiguration);

        arquillianFacet.setConfig(config);
    }

}
