package test.integration.support.assertions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;

public class ProjectAssert extends AbstractAssert<ProjectAssert, Project> {

    public ProjectAssert(Project actual) {
        super(actual, ProjectAssert.class);
    }

    public static ProjectAssert assertThat(Project project) {
        return new ProjectAssert(project);
    }

    public DirectDependencyAssert hasDirectDependency(String gav) {
        return new DirectDependencyAssert(actual, gav);
    }

    public EffectiveDependencyAssert hasEffectiveDependency(String gav) {
        return new EffectiveDependencyAssert(actual, gav);
    }

    public ManagedDependencyAssert hasDirectManagedDependency(String gav) {
        return new ManagedDependencyAssert(actual, gav);
    }

    public ConfigurationAssert hasConfiguration() {
        final ConfigurationFacet facet = actual.getFacet(ConfigurationFacet.class);
        Assertions.assertThat(facet).isNotNull();

        final Configuration configuration = facet.getConfiguration();
        return new ConfigurationAssert(configuration);
    }

    public ArquillianConfigAssert hasArquillianConfig() {
        ResourcesFacet resources = actual.getFacet(ResourcesFacet.class);
        final FileResource<?> testResource = resources.getTestResource("arquillian.xml");

        Assertions.assertThat(testResource.exists()).isTrue();

        final ArquillianConfig config = new ArquillianConfig(testResource.getResourceInputStream());

        return new ArquillianConfigAssert(config);
    }

    public ProjectAssert hasTestResources(String... fileNames) {
        final ResourcesFacet resource = actual.getFacet(ResourcesFacet.class);
        final List<Resource<?>> resources = resource.getTestResourceDirectory().listResources();

        Assertions.assertThat(resources).isNotEmpty();

        final List<String> testResourcesName = resources.stream().map(Resource::getName).collect(Collectors.toList());

        Assertions.assertThat(testResourcesName).contains(fileNames);

        return this;
    }

}
