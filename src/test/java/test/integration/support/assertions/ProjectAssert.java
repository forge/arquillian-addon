package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.jboss.forge.addon.projects.Project;

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
}
