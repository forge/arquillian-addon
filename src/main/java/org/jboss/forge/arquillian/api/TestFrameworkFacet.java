/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.api;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@FacetConstraint(ArquillianFacet.class)
@FacetConstraint(JavaSourceFacet.class)
public abstract class TestFrameworkFacet extends AbstractVersionedFacet {

    private boolean standalone = false;

    public abstract String getTemplateLocation();

    public abstract String getTemplateStandaloneLocation();

    public abstract String getFrameworkName();

    public abstract String getVersionPropertyName();

    public abstract DependencyBuilder createFrameworkDependency();

    public abstract DependencyBuilder createArquillianDependency();

    public abstract DependencyBuilder createArquillianStandaloneDependency();

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    @Override
    public boolean install() {
        if (getVersion() != null) {
            installDependencies();
            return true;
        }
        return isInstalled();
    }

    @Override
    public boolean isInstalled() {
        return hasEffectiveDependency(createFrameworkDependency())
            && (hasEffectiveDependency(createArquillianDependency()) || hasEffectiveDependency(createArquillianStandaloneDependency()));
    }

    @Override
    public boolean uninstall() {
        return false;
    }

    @Override
    protected Coordinate getVersionedCoordinate() {
        return createFrameworkDependency().getCoordinate();
    }

    protected void installDependencies() {
        if (standalone) {
            installArquillianDependency(createArquillianStandaloneDependency());
        } else {
            installArquillianDependency(createArquillianDependency());
        }
        installFrameworkDependency(createFrameworkDependency());
    }

    protected void installArquillianDependency(DependencyBuilder arquillianDependency) {
        if (hasEffectiveDependency(arquillianDependency)) {
            return;
        }
        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);

        if (arquillianDependency != null) {
            dependencyFacet.addDirectDependency(arquillianDependency);
        }
    }

    protected void installFrameworkDependency(DependencyBuilder frameworkDependency) {
        if (hasEffectiveDependency(frameworkDependency)) {
            return;
        }

        final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
        final MetadataFacet metadataFacet = getFaceted().getFacet(MetadataFacet.class);

        metadataFacet.setDirectProperty(getVersionPropertyName(), getVersion());
        dependencyFacet.addDirectDependency(frameworkDependency.setVersion(wrap(getVersionPropertyName())));
    }

}
