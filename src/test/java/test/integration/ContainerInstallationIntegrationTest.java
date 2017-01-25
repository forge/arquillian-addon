/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
@RunWith(Arquillian.class)
public class ContainerInstallationIntegrationTest 
{
   private ProjectFactory projectFactory;
   private UITestHarness uiTestHarness;
   private ShellTest shellTest;
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void setUp() throws Exception {
      AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
      projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
      uiTestHarness = addonRegistry.getServices(UITestHarness.class).get();
      shellTest = addonRegistry.getServices(ShellTest.class).get();
      facetFactory = addonRegistry.getServices(FacetFactory.class).get();
      final List<Class<? extends ProjectFacet>> facetTypes = Arrays.asList(ArquillianFacet.class, JavaSourceFacet.class);
      project = projectFactory.createTempProject(facetTypes);

   }

   @After
   public void tearDown() throws Exception {
      if (shellTest != null) {
         shellTest.close();
      }
   }

   @Test
   public void installOpenEJBContainer() throws Exception
   {
      installContainer("openejb-embedded-3.1",
              Arrays.asList(
                      new DependencyMatcher("arquillian-openejb-embedded-3.1"),
                      new DependencyMatcher("openejb-core")));
   }

   @Test
   public void installOpenWebBeansContainer() throws Exception
   {
      installContainer("openwebbeans-embedded-1",
              Arrays.asList(
                      new DependencyMatcher("arquillian-openwebbeans-embedded-1"),
                      new DependencyMatcher("openwebbeans-impl")));
   }

   @Test
   public void installGlassfishEmbeddedContainer() throws Exception
   {
      installContainer("glassfish-embedded-3.1",
              Arrays.asList(
                      new DependencyMatcher("arquillian-glassfish-embedded-3.1"),
                      new DependencyMatcher("glassfish-embedded-all")));
   }

   @Test
   public void installGlassfishManagedContainer() throws Exception
   {
      installContainer("glassfish-managed-3.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-glassfish-managed-3.1")));
   }

   @Test
   public void installGlassfishRemoteContainer() throws Exception
   {
      installContainer("glassfish-remote-3.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-glassfish-remote-3.1")));
   }

   @Test
   public void installJBossAS51ManagedContainer() throws Exception
   {
      installContainer("jbossas-managed-5.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-managed-5.1")));
   }

   @Test
   public void installJBossAS51RemoteContainer() throws Exception
   {
      installContainer("jbossas-remote-5.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-remote-5.1")));
   }

   @Test
   public void installJBossAS5RemoteContainer() throws Exception
   {
      installContainer("jbossas-remote-5",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-remote-5")));
   }

   @Test
   public void installJBossAS6EmbeddedContainer() throws Exception
   {
      installContainer("jbossas-embedded-6",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-embedded-6")));
   }

   @Test
   public void installJBossAS6ManagedContainer() throws Exception
   {
      installContainer("jbossas-managed-6",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-managed-6")));
   }

   @Test
   public void installJBossAS6RemoteContainer() throws Exception
   {
      installContainer("jbossas-remote-6",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-jbossas-remote-6")));
   }

   @Test
   public void installJBossAS7ManagedContainer() throws Exception
   {
      installContainer("jbossas-managed-7",
              Collections.singletonList(
                      new DependencyMatcher("jboss-as-arquillian-container-managed")));
   }

   @Test
   public void installJBossAS7RemoteContainer() throws Exception
   {
      installContainer("jbossas-remote-7",
              Collections.singletonList(
                      new DependencyMatcher("jboss-as-arquillian-container-remote")));
   }

   @Test
   public void installWildFlyManagedContainer() throws Exception
   {
      installContainer("wildfly-managed",
              Collections.singletonList(
                      new DependencyMatcher("wildfly-arquillian-container-managed")));
   }

   @Test
   public void installWildFlyRemoteContainer() throws Exception
   {
      installContainer("wildfly-remote",
              Collections.singletonList(
                      new DependencyMatcher("wildfly-arquillian-container-remote")));
   }

   @Test
   public void installJetty6EmbeddedContainer() throws Exception
   {
      installContainer("jetty-embedded-6.1",
              Arrays.asList(
                      new DependencyMatcher("arquillian-jetty-embedded-6.1"),
                      new DependencyMatcher("jetty")));
   }

   @Test
   public void installJetty7EmbeddedContainer() throws Exception
   {
      installContainer("jetty-embedded-7",
              Arrays.asList(
                      new DependencyMatcher("arquillian-jetty-embedded-7"),
                      new DependencyMatcher("jetty-webapp")));
   }

   @Test
   public void installTomcat6EmbeddedContainer() throws Exception
   {
      installContainer("tomcat-embedded-6",
              Arrays.asList(
                      new DependencyMatcher("arquillian-tomcat-embedded-6"),
                      new DependencyMatcher("catalina"),
                      new DependencyMatcher("catalina"),
                      new DependencyMatcher("coyote"),
                      new DependencyMatcher("jasper")));
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS7RemoteContainer() throws Exception
   {
      installContainer("was-remote-7",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-was-remote-7")));
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS8EmbeddedContainer() throws Exception
   {
      installContainer("was-embedded-8",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-was-embedded-8")));
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS8RemoteContainer() throws Exception
   {
      installContainer("was-remote-8",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-was-remote-8")));
   }

   @Test
   public void installTomcat6RemoteContainer() throws Exception
   {
      installContainer("tomcat-remote-6",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-tomcat-remote-6")));
   }

   @Test
   public void installWeldEEEmbeddedContainer() throws Exception
   {
      installContainer("weld-ee-embedded-1.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-weld-ee-embedded-1.1")));
   }

   @Test
   public void installWeldSEEmbeddedContainer() throws Exception
   {
      installContainer("weld-se-embedded-1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-weld-se-embedded-1")));
   }

   @Test
   public void installWeldSEEmbedded1_1Container() throws Exception
   {
      installContainer("weld-se-embedded-1.1",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-weld-se-embedded-1.1")));
   }

   @Test
   public void installWWeblogicRemoteContainer() throws Exception
   {
      installContainer("wls-remote-10.3",
              Collections.singletonList(
                      new DependencyMatcher("arquillian-wls-remote-10.3")));
   }

   private void installContainer(final String container, final List<DependencyMatcher> dependencyMatchers) throws Exception {

      shellTest.getShell().setCurrentResource(project.getRoot());

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter " + container + " --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      final Profile profile = mavenFacet.getModel().getProfiles().get(0);
      System.out.println(profile);
      System.out.println("dependnecie::" + profile.getDependencies());

      for (DependencyMatcher dependency : dependencyMatchers)
      {
         assertThat(profile.getDependencies(), hasItem(dependency));
      }

   }


   public static class DependencyMatcher extends BaseMatcher<Dependency>
   {
      private final String artifactId;

      public DependencyMatcher(final String artifactId)
      {
         this.artifactId = artifactId;
      }

      @Override
      public boolean matches(final Object o)
      {
         Dependency d = (Dependency) o;
         return d.getArtifactId().equals(artifactId);
      }

      @Override
      public void describeTo(final Description description)
      {
      }
   }
}
