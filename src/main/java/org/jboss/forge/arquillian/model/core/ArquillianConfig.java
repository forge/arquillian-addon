package org.jboss.forge.arquillian.model.core;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

import java.io.InputStream;
import java.util.Map;

public class ArquillianConfig {

    private static final String CONTAINER = "container";
    private static final String CONTAINER_QUALIFIER = "container@qualifier=";
    private static final String EXTENSION = "extension";
    private static final String EXTENSION_QUALIFIER = "extension@qualifier=";
    private static final String QUALIFIER = "qualifier";
    private static final String PROPERTY_NAME = "property@name=";
    private static final String CONFIGURATION = "configuration";

    private Node xml;

    public ArquillianConfig() {
        xml = XMLParser
            .parse("<arquillian xmlns=\"http://jboss.org/schema/arquillian\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "            xsi:schemaLocation=\"http://jboss.org/schema/arquillian http://jboss.org/schema/arquillian/arquillian_1_0.xsd\"></arquillian>");
    }

    public ArquillianConfig(InputStream is) {
        xml = XMLParser.parse(is);
    }

    public static void addPropertyToArquillianConfig(Node xml, String container, String key, String value) {
        xml.getOrCreate(CONTAINER_QUALIFIER + container)
            .getOrCreate(CONFIGURATION)
            .getOrCreate(PROPERTY_NAME + key)
            .text(value);
    }

    public boolean addExtension(String qualifier) {
        Node containerConfig = xml.getSingle(EXTENSION_QUALIFIER + qualifier);
        if (containerConfig == null) {
            xml.createChild(EXTENSION_QUALIFIER + qualifier);
            return true;
        }
        return false;
    }

    public boolean addExtensionProperty(String qualifier, String key, String value) {
        xml.getOrCreate(EXTENSION_QUALIFIER + qualifier)
            .getOrCreate(PROPERTY_NAME + key)
            .text(value);
        return true;
    }

    public boolean addExtensionProperty(String qualifier, Map<String, String> map) {
        final Node node = xml.getOrCreate(EXTENSION_QUALIFIER + qualifier);
        map.forEach(
            (key, value) -> node.getOrCreate(PROPERTY_NAME + key).text(value)
        );
        return true;
    }

    public boolean addContainer(String containerId) {
        Node containerConfig = xml.getSingle(CONTAINER_QUALIFIER + containerId);
        if (containerConfig == null) {
            xml.createChild(CONTAINER_QUALIFIER + containerId);
            return true;
        }
        return false;
    }

    public boolean addContainerProperty(String container, String key, String value) {
        xml.getOrCreate(CONTAINER_QUALIFIER + container)
            .getOrCreate(CONFIGURATION)
            .getOrCreate(PROPERTY_NAME + key)
            .text(value);
        return true;
    }

    public void addContainerWithAttribute(String containerId, String attribute, String value) {
        Node containerConfig = xml.getSingle("containerConfig@qualifier=" + containerId);

        if (containerConfig == null) {
            createContainerWithAttribute(containerId, attribute, value);
        }
    }

    private Node createContainerWithAttribute(String containerId, String key, String value) {
        Node container = xml.createChild(CONTAINER_QUALIFIER + containerId);

        if (key != null && value != null) {
            container.attribute(key, value);
        }

        return container;
    }

    public Node getExtensionNode(String qualifier) {
        return xml.get(EXTENSION).stream()
            .filter(node -> node.getAttribute(QUALIFIER).equals(qualifier))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Extension with qualifier: " + qualifier + " not found."));
    }

    public Node getContainerNode(String qualifier) {
        return xml.get(CONTAINER).stream()
            .filter(node -> node.getAttribute(QUALIFIER).equals(qualifier))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Container with qualifier: " + qualifier + " not found."));
    }

    public boolean isExtensionRegistered(String qualifier) {
        return xml.get(EXTENSION)
            .stream()
            .map(n -> n.getAttribute(QUALIFIER))
            .anyMatch(qualifier::equals);
    }

    public boolean isContainerRegistered(String qualifier) {
        return xml.get(CONTAINER)
            .stream()
            .map(n -> n.getAttribute(QUALIFIER))
            .anyMatch(qualifier::equals);
    }

    public boolean containsDefaultContainer() {
        return xml.get(CONTAINER)
            .stream()
            .map(n -> n.getAttribute("default"))
            .anyMatch(v -> v.equals("true"));
    }

    public String getContentOfNode(String node) {
        final Node single = xml.getSingle(node);

        if (single == null) {
            return null;
        }

        return single.getText();
    }

    @Override
    public String toString() {
        return XMLParser.toXMLString(xml);
    }
}
