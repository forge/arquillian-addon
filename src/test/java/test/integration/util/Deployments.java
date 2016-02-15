/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration.util;

import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class Deployments
{

   public static AddonArchive basicPluginInfrastructure()
   {
      AddonArchive archive = ShrinkWrap
            .create(AddonArchive.class)
            .addBeansXML()
            .addAsAddonDependencies(
                     AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                     AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                     AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                     AddonDependencyEntry.create("org.arquillian.forge:arquillian-addon"),
                     AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
            ).addClasses(DependencyMatcher.class);

      return archive;
   }
}
