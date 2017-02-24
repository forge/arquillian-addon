package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

public abstract class DependencyAssert<T extends AbstractAssert<T, DependencyBuilder>> extends AbstractAssert<T, DependencyBuilder> {

    protected final DependencyFacet dependencyFacet;
    private String type = "jar";
    private String scope = "compile";

    public DependencyAssert(DependencyBuilder actual, DependencyFacet facet, Class<?> selfType) {
        super(actual, selfType);
        this.dependencyFacet = facet;
    }

    public abstract Dependency getDependency(DependencyBuilder dependencyBuilder);

    public abstract boolean hasDependency(DependencyBuilder dependencyBuilder);

    public DependencyAssert<T> withType(String type) {
        this.type = type;
        final DependencyBuilder dependencyBuilder = createDependency();
        Assertions.assertThat(hasDependency(dependencyBuilder)).isTrue();
        return this;
    }

    public DependencyAssert<T> withScope(String scope) {
        this.scope = scope;
        final DependencyBuilder dependencyBuilder = createDependency();
        final Dependency actualDirectDependency = getDependency(dependencyBuilder);
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
