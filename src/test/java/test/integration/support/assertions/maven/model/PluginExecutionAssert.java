package test.integration.support.assertions.maven.model;

import org.apache.maven.model.PluginExecution;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class PluginExecutionAssert extends AbstractAssert<PluginExecutionAssert, PluginExecution> {

    public PluginExecutionAssert(PluginExecution actual) {
        super(actual, PluginExecutionAssert.class);
    }

    public PluginExecutionAssert hasGoals(String... expectedGoals) {
        final List<String> goals = actual.getGoals();
        Assertions.assertThat(goals).containsExactlyInAnyOrder(expectedGoals);

        return this;
    }

    public PluginExecutionAssert hasPhase(String expectedPhase) {
        Assertions.assertThat(actual.getPhase()).isEqualTo(expectedPhase);

        return this;
    }

    public PluginExecutionAssert hasConfigurarion(String expectedPhase) {
        Assertions.assertThat(actual.getConfiguration().toString()).contains(expectedPhase);

        return this;
    }
}
