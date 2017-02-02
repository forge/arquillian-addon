package test.integration.support.assertions.maven.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;


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

    public DependencyAssert hasDependency(String gav) {
        return new DependencyAssert(createDependency(gav));
    }

    private Dependency createDependency(String identifier) {
        Dependency dependency = new Dependency();
        if(identifier != null) {
            String[] split = identifier.split(":");
            if(split.length > 0) {
                dependency.setGroupId(split[0].trim());
            }

            if(split.length > 1) {
                dependency.setArtifactId(split[1].trim());
            }

            if(split.length > 2) {
                dependency.setVersion(split[2].trim());
            }

            String trimmed;
            if(split.length > 3) {
                trimmed = split[3].trim();
                dependency.setScope(trimmed);
            }

            if(split.length > 4) {
                trimmed = split[4].trim();
                dependency.setType(trimmed);
            }
        }

        return dependency;
    }
}
