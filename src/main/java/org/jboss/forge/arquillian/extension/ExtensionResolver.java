package org.jboss.forge.arquillian.extension;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.api.Extension;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtensionResolver {

    @Inject
    private DependencyResolver resolver;

    /**
     * @param project
     * @return Collection of all supported Extensions
     */
    public Collection<Extension> getAvailableExtensions(Project project) {
        return resolveAvailableExtensions(project);
    }

    /**
     * @param project
     * @return Collection of all non installed Extensions
     */
    public Collection<Extension> getInstallableExtensions(Project project) {
        Collection<Extension> availableExtensions = getAvailableExtensions(project);
        return resolveInstallableExtensions(getInstalledExtensions(project, availableExtensions), availableExtensions);
    }

    /**
     * @param project
     * @return Collection of all installed Extensions
     */
    public Collection<Extension> getInstalledExtensions(Project project) {
        return resolveInstalledExtensions(project, getAvailableExtensions(project));
    }

    private Collection<Extension> getInstalledExtensions(Project project, Collection<Extension> availableExtensions) {
        return resolveInstalledExtensions(project, availableExtensions);
    }

    private Collection<Extension> resolveInstalledExtensions(Project project, Collection<Extension> availableExtensions) {
        DependencyFacet dependencies = project.getFacet(DependencyFacet.class);

        List<Extension> installedExtensions = new ArrayList<>();
        for (Dependency installed : dependencies.getDependencies()) {
            for (Extension available : availableExtensions) {
                if (available.isDependency(installed)) {
                    installedExtensions.add(available);
                    break;
                }
            }
        }
        return installedExtensions;
    }

    private Collection<Extension> resolveInstallableExtensions(Collection<Extension> installedExtensions, Collection<Extension> availableExtensions) {
        List<Extension> installable = new ArrayList<>();

        for (Extension available : availableExtensions) {
            boolean found = false;
            for (Extension installed : installedExtensions) {
                if (available.equals(installed)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                installable.add(available);
            }
        }
        return installable;
    }

    private Collection<Extension> resolveAvailableExtensions(Project project) {
        if (!project.hasFacet(ArquillianFacet.class)) {
            throw new IllegalStateException("Project is missing Arquillian. Please install Arquillian before adding Extensions.");
        }

        MavenFacet maven = project.getFacet(MavenFacet.class);
        MavenModelResource thisPom = maven.getModelResource();

        ArquillianFacet arquillian = project.getFacet(ArquillianFacet.class);
        Dependency universeDep = arquillian.getInstalledBOM();

        // Resolve to find artifact to get to File on filesystem
        universeDep = resolver.resolveArtifact(DependencyQueryBuilder.create(universeDep.getCoordinate()));

        MavenModelResource universePom = (MavenModelResource) thisPom.createFrom(new File(universeDep.getArtifact().getFullyQualifiedName()));
        Model universe = universePom.getCurrentModel();

        List<Extension> extensions = new ArrayList<>();

        for (org.apache.maven.model.Dependency modules : universe.getDependencyManagement().getDependencies()) {
            extensions.add(new Extension(
                DependencyBuilder.create()
                    .setGroupId(modules.getGroupId())
                    .setArtifactId(modules.getArtifactId())
                    .setScopeType("test")
                    .setPackaging("pom")));
        }

        return extensions;
    }

}
