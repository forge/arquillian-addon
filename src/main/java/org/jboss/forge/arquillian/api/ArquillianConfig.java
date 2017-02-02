package org.jboss.forge.arquillian.api;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

import java.io.InputStream;

public class ArquillianConfig {

    private Node xml;


    public ArquillianConfig() {
        xml = XMLParser
                .parse("<arquillian xmlns=\"http://jboss.org/schema/arquillian\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "            xsi:schemaLocation=\"http://jboss.org/schema/arquillian http://jboss.org/schema/arquillian/arquillian_1_0.xsd\"></arquillian>");
    }

    public ArquillianConfig(InputStream is) {
        xml = XMLParser.parse(is);
    }

    public boolean addContainer(String containerId) {
        Node containerConfig = xml.getSingle("container@qualifier=" + containerId);
        if (containerConfig == null) {
            xml.createChild("container@qualifier=" + containerId);
            return true;
        }
        return false;
    }

    public boolean isContainerWithDefault(String value) {
        return xml.get("container")
                .stream()
                .map(n -> n.getAttribute("default"))
                .filter(v -> v.equals(value))
                .findAny()
                .isPresent();
    }

    public void addContainerWithAttribute(String containerId, String attribute, String value) {

        Node containerConfig = xml.getSingle("containerConfig@qualifier=" + containerId);
        if (containerConfig == null) {
            createContainerWithAttribute(containerId, attribute, value);
        }
    }

    private Node createContainerWithAttribute(String containerId, String key, String value) {
        Node container = xml.createChild("container@qualifier=" + containerId);

        if (key != null && value != null) {
            container.attribute(key, value);
        }

        return container;
    }

    public boolean addContainerProperty(String container, String key, String value) {
        xml.getOrCreate("container@qualifier=" + container)
                .getOrCreate("configuration")
                .getOrCreate("property@name=" + key)
                .text(value);
        return true;
    }

    public static void addPropertyToArquillianConfig(Node xml, String container, String key, String value) {
        xml.getOrCreate("container@qualifier=" + container)
                .getOrCreate("configuration")
                .getOrCreate("property@name=" + key)
                .text(value);
    }

    @Override
    public String toString() {
        return XMLParser.toXMLString(xml);
    }
}
