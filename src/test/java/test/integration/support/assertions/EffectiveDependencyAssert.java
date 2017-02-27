package test.integration.support.assertions;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

public class EffectiveDependencyAssert extends DependencyAssert<EffectiveDependencyAssert> {

    public EffectiveDependencyAssert(Project project, String gav) {
        super(DependencyBuilder.create(gav), project.getFacet(DependencyFacet.class), EffectiveDependencyAssert.class);
    }

    @Override
    public Dependency getDependency(DependencyBuilder dependencyBuilder) {
        return dependencyFacet.getEffectiveDependency(dependencyBuilder);
    }

    @Override
    public boolean hasDependency(DependencyBuilder dependencyBuilder) {
        return dependencyFacet.hasEffectiveDependency(dependencyBuilder);
    }
}
