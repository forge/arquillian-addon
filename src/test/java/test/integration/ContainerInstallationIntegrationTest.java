/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import java.util.concurrent.TimeoutException;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class ContainerInstallationIntegrationTest extends ShellTestTemplate
{

   @Test
   public void installOpenEJBContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("openejb-embedded-3.1",
              "org.jboss.arquillian.container:arquillian-openejb-embedded-3.1",
              "org.apache.openejb:openejb-core");

   }

   @Test
   public void installOpenWebBeansContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("openwebbeans-embedded-1",
              "org.jboss.arquillian.container:arquillian-openwebbeans-embedded-1",
              "org.apache.openwebbeans:openwebbeans-impl");
   }

   @Test
   public void installGlassfishEmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("glassfish-embedded-3.1",
              "org.jboss.arquillian.container:arquillian-glassfish-embedded-3.1",
              "org.glassfish.extras:glassfish-embedded-all");
   }

   @Test
   public void installGlassfishManagedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("glassfish-managed-3.1",
              "org.jboss.arquillian.container:arquillian-glassfish-managed-3.1");
   }

   @Test
   public void installGlassfishRemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("glassfish-remote-3.1",
              "org.jboss.arquillian.container:arquillian-glassfish-remote-3.1");
   }

   @Test
   public void installJBossAS51ManagedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-managed-5.1",
              "org.jboss.arquillian.container:arquillian-jbossas-managed-5.1");
   }

   @Test
   public void installJBossAS51RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-remote-5.1",
              "org.jboss.arquillian.container:arquillian-jbossas-remote-5.1");
   }

   @Test
   public void installJBossAS5RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-remote-5",
              "org.jboss.arquillian.container:arquillian-jbossas-remote-5");
   }

   @Test
   public void installJBossAS6EmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-embedded-6",
              "org.jboss.arquillian.container:arquillian-jbossas-embedded-6");
   }

   @Test
   public void installJBossAS6ManagedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-managed-6",
              "org.jboss.arquillian.container:arquillian-jbossas-managed-6");
   }

   @Test
   public void installJBossAS6RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jbossas-remote-6",
              "org.jboss.arquillian.container:arquillian-jbossas-remote-6");
   }

   @Test
   public void installJBossAS7ManagedContainer() throws Exception
   {
      installContainerUsingChameleon("jbossas-managed-7");
   }

   @Test
   public void installJBossAS7RemoteContainer() throws Exception
   {
      installContainerUsingChameleon("jbossas-remote-7");
   }

   @Test
   public void installWildFlyManagedContainer() throws Exception
   {
      installContainerUsingChameleon("wildfly-managed");
   }

   @Test
   public void installWildFlyRemoteContainer() throws Exception
   {
      installContainerUsingChameleon("wildfly-remote");
   }

   @Test
   public void installJetty6EmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jetty-embedded-6.1",
              "org.jboss.arquillian.container:arquillian-jetty-embedded-6.1",
              "org.mortbay.jetty:jetty");
   }

   @Test
   public void installJetty7EmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("jetty-embedded-7",
              "org.jboss.arquillian.container:arquillian-jetty-embedded-7",
              "org.eclipse.jetty:jetty-webapp");
   }

   @Test
   public void installTomcat6EmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("tomcat-embedded-6",
               "org.jboss.arquillian.container:arquillian-tomcat-embedded-6",
               "org.apache.tomcat:catalina",
               "org.apache.tomcat:coyote",
               "org.apache.tomcat:jasper");
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS7RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("was-remote-7",
              "org.jboss.arquillian.container:arquillian-was-remote-7");
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS8EmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("was-embedded-8",
              "org.jboss.arquillian.container:arquillian-was-embedded-8");
   }

   @Test
   @Ignore("Not in default maven repo")
   public void installWAS8RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("was-remote-8",
              "org.jboss.arquillian.container:arquillian-was-remote-8");
   }

   @Test
   public void installTomcat6RemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("tomcat-remote-6",
              "org.jboss.arquillian.container:arquillian-tomcat-remote-6");
   }

   @Test
   public void installWeldEEEmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("weld-ee-embedded-1.1",
              "org.jboss.arquillian.container:arquillian-weld-ee-embedded-1.1");
   }

   @Test
   public void installWeldSEEmbeddedContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("weld-se-embedded-1",
              "org.jboss.arquillian.container:arquillian-weld-se-embedded-1");
   }

   @Test
   public void installWeldSEEmbedded1_1Container() throws Exception
   {
      installContainerAssertProfileAndDependencies("weld-se-embedded-1.1",
              "org.jboss.arquillian.container:arquillian-weld-se-embedded-1.1");
   }

   @Test
   public void installWWeblogicRemoteContainer() throws Exception
   {
      installContainerAssertProfileAndDependencies("wls-remote-10.3",
              "org.jboss.arquillian.container:arquillian-wls-remote-10.3");
   }

   private void installContainerAssertProfileAndDependencies(final String container, String... dependencies) throws Exception {


      shell().execute("arquillian-setup --container-adapter " + container + " --test-framework junit");

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      final Profile profile = mavenFacet.getModel().getProfiles().get(0);
      final String profileId = "arquillian-" + container;

      assertThat(profile).hasId(profileId);

      for (String dependency : dependencies) {
         String[] gav = dependency.split(":");
         assertThat(profile).hasDependency(dependency).withGroupId(gav[0]).withArtifactId(gav[1]);
      }

      assertJunitAndUniverseDependency();
   }

   private void installContainerUsingChameleon(final  String container) throws TimeoutException {
      shell().execute("arquillian-setup --container-adapter " + container + " --test-framework junit");

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      final Profile profile = mavenFacet.getModel().getProfiles().get(0);
      final Plugin surefirePlugin = profile.getBuild().getPlugins().get(0);

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      final FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      assertJunitAndUniverseDependency();
      assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-chameleon").withType("pom").withScope("test");
      assertThat(arquillianXML.getContents()).contains("<property name=\"chameleonTarget\">${chameleon.target}</property>");

   }

   private void assertJunitAndUniverseDependency() {
      assertThat(project).hasDirectDependency("junit:junit").withType("jar").withScope("test");
      assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-junit").withType("pom").withScope("test");
      assertThat(project).hasDirectManagedDependency("org.arquillian:arquillian-universe").withType("pom").withScope("import");

   }
}
