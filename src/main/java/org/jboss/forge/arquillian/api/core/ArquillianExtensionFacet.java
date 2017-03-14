package org.jboss.forge.arquillian.api.core;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.extension.Extension;
import org.jboss.forge.arquillian.extension.ExtensionResolver;

import javax.inject.Inject;
import java.util.Collection;

@FacetConstraints({
    @FacetConstraint(DependencyFacet.class),
    @FacetConstraint(MetadataFacet.class),
    @FacetConstraint(ProjectFacet.class),
    @FacetConstraint(ArquillianFacet.class)
})
public class ArquillianExtensionFacet extends AbstractVersionedFacet {

    @Inject
    private ExtensionResolver resolver;

    @Override
    protected Coordinate getVersionedCoordinate() {
        return getFaceted().getFacet(ArquillianFacet.class).getVersionedCoordinate();
    }

    @Override
    public boolean install() {
        return isInstalled();
    }

    @Override
    public boolean isInstalled() {
        return getFaceted().hasFacet(ArquillianFacet.class);
    }

    @Override
    public boolean uninstall() {
        return false;
    }

    public Collection<Extension> getInstalledExtensions() {
        return resolver.getInstalledExtensions(getFaceted());
    }

    public Collection<Extension> getAvailableExtensions() {
        return resolver.getAvailableExtensions(getFaceted());
    }

    public boolean isInstalled(Extension extension) {
        for (Extension installed : resolver.getInstalledExtensions(getFaceted())) {
            if (installed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    public void install(Extension extension) {
        if (isInstalled(extension) || extension == null) {
            return;
        }
        getFaceted().getFacet(DependencyFacet.class).addDirectDependency(extension.getDependency());
    }
}
