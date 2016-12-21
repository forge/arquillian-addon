/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.arquillian.command;

import org.jboss.forge.roaster.model.util.Types;

/**
 * The Archive type to be generated in the tests
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum ArchiveType
{
   JAR("org.jboss.shrinkwrap.api.spec.JavaArchive","addAsManifestResource"),
   WAR("org.jboss.shrinkwrap.api.spec.WebArchive","addAsWebInfResource");

   private final String className;
   private final String simpleClassName;
   private final String beansXmlLocationAdder;

   ArchiveType(String className, String beansXmlLocationAdder)
   {
      this.className = className;
      this.simpleClassName = Types.toSimpleName(className);
      this.beansXmlLocationAdder = beansXmlLocationAdder;
   }

   public String getClassName()
   {
      return className;
   }

   public String getSimpleClassName()
   {
      return simpleClassName;
   }

   public String getBeansXmlLocationAdder()
   {
      return beansXmlLocationAdder;
   }




}
