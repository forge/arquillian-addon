package test.integration.support.assertions.maven.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;


public class ProfileAssert extends AbstractAssert<ProfileAssert, Profile> {

    public ProfileAssert(Profile actual) {
        super(actual, ProfileAssert.class);
    }

    public static ProfileAssert assertThat(Profile actual) {
        return new ProfileAssert(actual);
    }

    public ProfileAssert hasId(String profileId) {
        Assertions.assertThat(actual.getId()).isNotNull();
        Assertions.assertThat(actual.getId()).isEqualTo(profileId);
        return this;
    }

    public DependencyAssert hasDependency(String ga) {
        final List<Dependency> dependencies = actual.getDependencies();
        final String[] split = ga.split(":");
        final Dependency atualDependency = dependencies.stream()
            .filter(dependency -> dependency.getGroupId().equals(split[0]) && dependency.getArtifactId().equals(split[1]))
            .findFirst()
            .orElseThrow(() -> new AssertionError("No dependency found with groupId: " + split[0] + " artifactId: " + split[1] + " in profile: " + actual.getId()));

        return new DependencyAssert(atualDependency);
    }

    public ProfileAssert hasBuild() {
        Assertions.assertThat(actual.getBuild()).isNotNull();

        return this;
    }

    public ProfileAssert hasPluginSize(Integer size) {
        final List<Plugin> pluginList = actual.getBuild().getPlugins();
        Assertions.assertThat(pluginList).hasSize(size);

        return this;
    }

}
