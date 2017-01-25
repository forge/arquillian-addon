package test.integration;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ConfigurationIntegrationTest {

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
   public void should_configure_container() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter tomcat-embedded-6 --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      assertThat(arquillianXML.getContents(), containsString("<property name=\"bindHttpPort\">9090</property>"));
   }

   @Test
   public void should_configure_container_with_chameleon_if_chameleon_supported() throws Exception {
      shellTest.getShell().setCurrentResource(project.getRoot());

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      final DependencyBuilder chameleonDependency = DependencyBuilder.create().setGroupId("org.arquillian.universe")
              .setArtifactId("arquillian-chameleon").setScopeType("test").setPackaging("pom");
      assertThat(dependencyFacet.hasDirectDependency(chameleonDependency), is(true));

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      assertThat(arquillianXML.getContents(), containsString("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:REMOTE</property>"));
   }

   @Test
   public void should_override_chameleon_target_if_chameleon_supported() throws Exception {
      shellTest.getShell().setCurrentResource(project.getRoot());

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      final DependencyBuilder chameleonDependency = DependencyBuilder.create().setGroupId("org.arquillian.universe")
              .setArtifactId("arquillian-chameleon").setScopeType("test").setPackaging("pom");
      assertThat(dependencyFacet.hasDirectDependency(chameleonDependency), is(true));

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      assertThat(arquillianXML.getContents(), containsString("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:REMOTE</property>"));

      final Result overrideChameleonTarget = shellTest.execute("arquillian-container-setup --container-adapter wildfly-managed", 30, TimeUnit.SECONDS);
      assertThat(overrideChameleonTarget, is(not(instanceOf(Failed.class))));
      assertThat(arquillianXML.getContents(), containsString("<property name=\"chameleonTarget\">wildfly:8.2.1.Final:MANAGED</property>"));
   }


   @Test
   public void should_override_configuration_options() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter tomcat-embedded-6 --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      Result resultConfigureContainer = shellTest.execute("arquillian-container-configuration --container arquillian-tomcat-embedded-6 --container-option bindHttpPort", 15, TimeUnit.SECONDS);
      assertThat(resultConfigureContainer, is(not(instanceOf(Failed.class))));
      assertThat(arquillianXML.getContents(), containsString("<property name=\"bindHttpPort\">9090</property>"));

      Result resultOverrideContainer = shellTest.execute("arquillian-container-configuration --container arquillian-tomcat-embedded-6 --container-option bindHttpPort --container-value 8081", 15, TimeUnit.SECONDS);
      assertThat(resultOverrideContainer, is(not(instanceOf(Failed.class))));

      assertThat(arquillianXML.getContents(), containsString("<property name=\"bindHttpPort\">8081</property>"));
   }

   @Test
   public void should_create_arquillian_xml_on_setup() throws Exception
   {

      shellTest.getShell().setCurrentResource(project.getRoot());

      final Result resultArquillianSetup = shellTest.execute("arquillian-setup --container-adapter wildfly-remote --test-framework junit", 30, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

      assertThat(arquillianXML, is(not(nullValue())));
      assertThat(arquillianXML.exists(), is(true));

   }

}
