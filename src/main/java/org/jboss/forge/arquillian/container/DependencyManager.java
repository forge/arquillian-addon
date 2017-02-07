package org.jboss.forge.arquillian.container;


import org.apache.maven.model.Model;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

public class DependencyManager {

    public void addChameleonDependency(Project project) {
        MavenFacet facet = project.getFacet(MavenFacet.class);
        Model pom = facet.getModel();
        if (!isInstalled(project)) {
            pom.getDependencies().add(
                new MavenDependencyAdapter(DependencyBuilder.create(createChameleonDep())));
        }

        facet.setModel(pom);
    }

    public DependencyBuilder createChameleonDep() {
        return DependencyBuilder.create().setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-chameleon").setScopeType("test").setPackaging("pom");
    }

    private boolean isInstalled(Project project) {
        final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        return dependencyFacet.hasEffectiveDependency(createChameleonDep());
    }
}
