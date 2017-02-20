package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.arquillian.api.ArquillianConfig;
import org.jboss.forge.parser.xml.Node;

import java.util.List;

public class ArquillianConfigAssert extends AbstractAssert<ArquillianConfigAssert, ArquillianConfig> {

    public ArquillianConfigAssert(ArquillianConfig actual) {
        super(actual, ArquillianConfigAssert.class);
    }

    public static ArquillianConfigAssert assertThat(ArquillianConfig actual) {
        return new ArquillianConfigAssert(actual);
    }

    public NodeAssert withExtension(String extension) {
        Assertions.assertThat(actual.isExtensionRegistered(extension)).isTrue();

        return new NodeAssert(actual.getNode(extension));
    }

    public static class NodeAssert extends AbstractAssert<NodeAssert, Node> {

        public NodeAssert(Node actual) {
            super(actual, NodeAssert.class);
        }

        public NodeAssert withProperty(String key, String value) {
            try {

                final String contentOfNode = getContentOfNode(key, actual.getChildren());
                Assertions.assertThat(contentOfNode.trim()).isEqualTo(value);

            } catch (IllegalStateException e) {
                Assertions.fail("couldn't find property by name: " + key);
            }

            return this;
        }

        private String getContentOfNode(String property, List<Node> nodes) {
            return nodes.stream()
                .filter(node -> node.getAttribute("name").equals(property))
                .map(Node::getText)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Node with property: " + property + " not found."));
        }
    }
}
