package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.SoftAssertions;
import org.jboss.forge.arquillian.model.core.ArquillianConfig;
import org.jboss.forge.parser.xml.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArquillianConfigAssert extends AbstractAssert<ArquillianConfigAssert, ArquillianConfig> {

    public ArquillianConfigAssert(ArquillianConfig actual) {
        super(actual, ArquillianConfigAssert.class);
    }

    public static ArquillianConfigAssert assertThat(ArquillianConfig actual) {
        return new ArquillianConfigAssert(actual);
    }

    public NodeAssert withExtension(String extension) {
        Assertions.assertThat(actual.isExtensionRegistered(extension)).isTrue();

        return new NodeAssert(actual.getExtensionNode(extension));
    }

    public NodeAssert withContainer(String container) {
        Assertions.assertThat(actual.isContainerRegistered(container)).isTrue();

        return new NodeAssert(actual.getContainerNode(container));
    }

    public static class NodeAssert extends AbstractAssert<NodeAssert, Node> {

        public NodeAssert(Node actual) {
            super(actual, NodeAssert.class);
        }

        public NodeAssert hasConfiguration() {
            final List<Node> nodes = actual.getChildren();
            final Node configuration = nodes.stream()
                .filter(node -> node.getName().equals("configuration"))
                .map(Function.identity())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Node with configuration not found."));

            return new NodeAssert(configuration);
        }

        public NodeAssert withProperty(String key, String value) {

            final String contentOfNode = getContentOfNode(key, actual.getChildren());
            Assertions.assertThat(contentOfNode.trim()).isEqualTo(value);

            return this;
        }

        public NodeAssert withProperties(String... keyValuePairs) {

            final Map<String, String> keyValue = Arrays.stream(keyValuePairs).map(keyValuePair -> keyValuePair.split(":")).
                collect(Collectors.toMap(keyValuePair -> keyValuePair[0], keyValuePair -> keyValuePair[1]));

            SoftAssertions softAssertions = new SoftAssertions();
            for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                final String contentOfNode = getContentOfNode(entry.getKey(), actual.getChildren());
                softAssertions.assertThat(contentOfNode.trim()).isEqualTo(entry.getValue());
            }

            softAssertions.assertAll();

            return this;
        }

        private String getContentOfNode(String property, List<Node> nodes) {
            return nodes.stream()
                .filter(node -> node.getAttribute("name").equals(property))
                .map(Node::getText)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Node with property: " + property + " not found."));
        }

        public ObjectArrayAssert<String> withProperty(String key) {
            final String contentOfNode = getContentOfNode(key, actual.getChildren());
            Assertions.assertThat(contentOfNode).isNotEmpty();

            final String[] strings = contentOfNode.trim().split(System.lineSeparator());

            return new ObjectArrayAssert<>(strings);
        }
    }
}
