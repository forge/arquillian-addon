package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

public class ManagedDependencyAssert extends AbstractAssert<ManagedDependencyAssert, DependencyBuilder> {

    private final DependencyFacet dependencyFacet;
    private String type = "jar";
    private String scope = "compile";

    public ManagedDependencyAssert(Project project, String gav) {
        super(DependencyBuilder.create(gav), ManagedDependencyAssert.class);
        this.dependencyFacet = project.getFacet(DependencyFacet.class);
    }

    public DependencyBuilder verify() {
        final DependencyBuilder dependencyBuilder = createDependency();
        Assertions.assertThat(dependencyFacet.hasDirectDependency(dependencyBuilder)).isTrue();
        return dependencyBuilder;
    }

    public ManagedDependencyAssert withType(String type) {
        this.type = type;
        final DependencyBuilder dependencyBuilder = createDependency();
        Assertions.assertThat(dependencyFacet.hasDirectManagedDependency(dependencyBuilder)).isTrue();
        return this;
    }

    public ManagedDependencyAssert withScope(String scope) {
        this.scope = scope;
        final DependencyBuilder dependencyBuilder = createDependency();
        final Dependency actualDirectManagedDependency = dependencyFacet.getDirectManagedDependency(dependencyBuilder);
        Assertions.assertThat(actualDirectManagedDependency).isNotNull();
        Assertions.assertThat(actualDirectManagedDependency.getScopeType()).isEqualTo(scope);
        return this;
    }

    private DependencyBuilder createDependency() {
        final DependencyBuilder dependencyBuilder = DependencyBuilder.create(this.actual);
        dependencyBuilder.setPackaging(type);
        dependencyBuilder.setScopeType(scope);
        return dependencyBuilder;
    }
}
