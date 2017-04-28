package org.jboss.forge.arquillian.container;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;

public class TomcatConfiguration {

    @Inject
    ResourceFactory resourceFactory;

    private static final Logger logger = Logger.getLogger(TomcatConfiguration.class.getName());
    private static final Pattern pattern = Pattern.compile("^\\d+");

    public void addContainerConfigurationAndSetResourcesContent(Project project, ArquillianConfig config, String version, String profileId) {

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
