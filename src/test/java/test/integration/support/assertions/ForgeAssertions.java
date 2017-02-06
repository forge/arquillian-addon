package test.integration.support.assertions;

import org.apache.maven.model.Profile;
import org.assertj.core.api.Assertions;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import test.integration.support.assertions.maven.model.ProfileAssert;

public class ForgeAssertions extends Assertions {

    public static ProjectAssert assertThat(Project project) {
        return new ProjectAssert(project);
    }

    public static MethodAssert assertThat(MethodSource source) {
        return new MethodAssert(source);
    }

    public static JavaSourceAssert assertThat(JavaClassSource source) {
        return new JavaSourceAssert(source);
    }

    public static AnnotationAssert assertThat(AnnotationSource source) {
        return new AnnotationAssert(source);
    }

    public static ProfileAssert assertThat(Profile profile) {
        return new ProfileAssert(profile);
    }
}
