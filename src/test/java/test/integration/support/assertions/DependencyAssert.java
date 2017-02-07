package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

public class DependencyAssert extends AbstractAssert<DependencyAssert, DependencyBuilder> {

    private final DependencyFacet dependencyFacet;
    private String type = "jar";
    private String scope = "compile";

    public DependencyAssert(Project project, String gav) {
        super(DependencyBuilder.create(gav), DependencyAssert.class);
        this.dependencyFacet = project.getFacet(DependencyFacet.class);
    }

    public DependencyBuilder verify() {
        final DependencyBuilder dependencyBuilder = createDependency();
        Assertions.assertThat(dependencyFacet.hasDirectDependency(dependencyBuilder)).isTrue();
        return dependencyBuilder;
    }

    public DependencyAssert withType(String type) {
        this.type = type;
        final DependencyBuilder dependencyBuilder = createDependency();
        Assertions.assertThat(dependencyFacet.hasDirectDependency(dependencyBuilder)).isTrue();
        return this;
    }

    public DependencyAssert withScope(String scope) {
        this.scope = scope;
        final DependencyBuilder dependencyBuilder = createDependency();
        final Dependency actualDirectDependency = dependencyFacet.getDirectDependency(dependencyBuilder);
        Assertions.assertThat(actualDirectDependency).isNotNull();
        Assertions.assertThat(actualDirectDependency.getScopeType()).isEqualTo(scope);
        return this;
    }

    private DependencyBuilder createDependency() {
        final DependencyBuilder dependencyBuilder = DependencyBuilder.create(this.actual);
        dependencyBuilder.setPackaging(type);
        dependencyBuilder.setScopeType(scope);
        return dependencyBuilder;
    }
}
