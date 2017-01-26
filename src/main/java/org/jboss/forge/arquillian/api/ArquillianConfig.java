package org.jboss.forge.arquillian.api;

import java.io.InputStream;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

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

   public boolean addExtension(String qualifier)
   {
      Node containerConfig = xml.getSingle("extension@qualifier=" + qualifier);
      if (containerConfig == null)
      {
         xml.createChild("extension@qualifier=" + qualifier);
         return true;
      }
      return false;
   }

   public boolean addExtensionProperty(String qualifier, String key, String value)
   {
      xml.getOrCreate("extension@qualifier=" + qualifier)
              .getOrCreate("property@name=" + key)
              .text(value);
      return true;
   }

   public boolean addContainer(String containerId)
   {
      Node containerConfig = xml.getSingle("container@qualifier=" + containerId);
      if (containerConfig == null)
      {
         xml.createChild("container@qualifier=" + containerId);
         return true;
      }
      return false;
   }
   
   public boolean addContainerProperty(String container, String key, String value)
   {
      xml.getOrCreate("container@qualifier=" + container)
         .getOrCreate("configuration")
         .getOrCreate("property@name=" + key)
         .text(value);
      return true;
   }

   public static void addPropertyToArquillianConfig(Node xml, String container, String key, String value)
   {
      xml.getOrCreate("container@qualifier=" + container)
         .getOrCreate("configuration")
         .getOrCreate("property@name=" + key)
         .text(value);
   }

   public boolean isExtensionRegistered(String qualifier)
   {
      return xml.get("extension")
              .stream()
              .map(n -> n.getAttribute("qualifier"))
              .filter(q -> qualifier.equals(q))
              .findAny().isPresent();
   }

   public String getContentOfNode(String node)
   {
      final Node single = xml.getSingle(node);

      if (single == null)
      {
         return null;
      }

      return single.getText();
   }

   @Override
   public String toString() {
      return XMLParser.toXMLString(xml);
   }
}
