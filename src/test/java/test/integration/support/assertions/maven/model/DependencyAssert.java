package test.integration.support.assertions.maven.model;


import org.apache.maven.model.Dependency;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class DependencyAssert extends AbstractAssert<DependencyAssert, Dependency> {

    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String classifier;
    private String scope;

    public DependencyAssert(Dependency actual){
        super(actual, DependencyAssert.class);
    }

    public static DependencyAssert assertThat(Dependency actual) {
        return new DependencyAssert(actual);
    }

    public DependencyAssert withGroupId(String groupId) {
        final String actualGroupId = actual.getGroupId();
        Assertions.assertThat(actualGroupId).isNotNull();
        Assertions.assertThat(actualGroupId).isEqualTo(groupId);
        return this;
    }

    public DependencyAssert withArtifactId(String artifactId) {
        final String actualArtifactId = actual.getArtifactId();
        Assertions.assertThat(actualArtifactId).isNotNull();
        Assertions.assertThat(actualArtifactId).isEqualTo(artifactId);
        return this;
    }

    public DependencyAssert withVersion(String version) {
        final String actualVersion = actual.getVersion();
        Assertions.assertThat(actualVersion).isNotNull();
        Assertions.assertThat(actualVersion).isEqualTo(version);
        return this;
    }

    public DependencyAssert withType(String type) {
       final String actualType = actual.getType();
        Assertions.assertThat(actualType).isNotNull();
        Assertions.assertThat(actualType).isEqualTo(type);
        return this;
    }

    public DependencyAssert withClassifier(String classifier) {
        final String actualClassifier = actual.getClassifier();
        Assertions.assertThat(actualClassifier).isNotNull();
        Assertions.assertThat(actualClassifier).isEqualTo(classifier);
        return this;
    }

    public DependencyAssert withScope(String scope) {
        final String actualScope = actual.getScope();
        Assertions.assertThat(actualScope).isNotNull();
        Assertions.assertThat(actualScope).isEqualTo(scope);
        return this;
    }

}
