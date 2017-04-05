package test.integration.support.assertions.maven.model;


import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class PluginAssert extends AbstractAssert<PluginAssert, Plugin> {

    public PluginAssert(Plugin actual) {
        super(actual, PluginAssert.class);
    }

    public static PluginAssert assertThat(Plugin actual) {
        return new PluginAssert(actual);
    }

    public PluginAssert hasGroupId(String groupId) {
        Assertions.assertThat(actual.getGroupId()).isEqualTo(groupId);

        return this;
    }

    public PluginAssert hasArtifactId(String artifactId) {
        Assertions.assertThat(actual.getArtifactId()).isEqualTo(artifactId);

        return this;
    }

    public PluginExecutionAssert hasExecutionWithId(String id) {
        final PluginExecution execution = actual.getExecutions().stream()
            .filter(pluginExecution -> id.equals(pluginExecution.getId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Execution with id:" + id + "is not present in plugin of Id: " + actual.getId()));

        return new PluginExecutionAssert(execution);
    }

}
