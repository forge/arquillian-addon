/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.command.AddTestFrameworkCommand;
import org.jboss.forge.arquillian.testframework.junit.JUnitFacet;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(Arquillian.class)
public class JUnitTestGenerationIntegrationTest {
   private ProjectFactory projectFactory;
   private UITestHarness uiTestHarness;
   private ShellTest shellTest;
   private FacetFactory facetFactory;

   private Project project;
   private DependencyFacet dependencyFacet;

   @Before
   public void setUp() throws Exception
   {
      AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
      projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
      uiTestHarness = addonRegistry.getServices(UITestHarness.class).get();
      shellTest = addonRegistry.getServices(ShellTest.class).get();
      facetFactory = addonRegistry.getServices(FacetFactory.class).get();
      final List<Class<? extends ProjectFacet>> facetTypes = Arrays.asList(ArquillianFacet.class, JavaSourceFacet.class, JUnitFacet.class);
      project = projectFactory.createTempProject(facetTypes);
      dependencyFacet = project.getFacet(DependencyFacet.class);
      shellTest.getShell().setCurrentResource(project.getRoot());
   }

   @After
   public void tearDown() throws Exception
   {
      if (shellTest != null) {
         shellTest.close();
      }
   }

   @Test
   public void shouldGenerateJUnitBasedTest() throws Exception
   {
      final JavaClass<?> testClass = testJUnitTestGenerationUsing("arquillian-setup --container-adapter glassfish-embedded-3.1 --test-framework junit");

      final DependencyBuilder universeJunitDependency = DependencyBuilder.create("org.arquillian.universe:arquillian-junit");
      universeJunitDependency.setPackaging("pom");
      assertThat(dependencyFacet.hasDirectDependency(universeJunitDependency), is(true));

      assertThat(testClass.hasField("bean"), is(true));

      final Method<?, ?> createDeployment = testClass.getMethod("createDeployment");
      assertThat(createDeployment, is(notNullValue()));
   }

   @Test @Ignore("This test is flaky for some unknown reason that we didn't realized why yet. To ot block the release we are going to test this feature manually and continue the research on that")
   public void shouldGenerateJUnitStandaloneBasedTest() throws Exception
   {

      final JavaClass<?> testClass = testJUnitTestGenerationUsing("arquillian-setup --standalone --test-framework junit");

      final DependencyBuilder universeJunitDependency = DependencyBuilder.create("org.arquillian.universe:arquillian-junit-standalone");
      universeJunitDependency.setPackaging("pom");
      assertThat(dependencyFacet.hasDirectDependency(universeJunitDependency), is(true));

      assertThat(testClass.hasField("bean"), is(false));

      final Method<?, ?> createDeployment = testClass.getMethod("createDeployment");
      assertThat(createDeployment, is(nullValue()));

   }

   private JavaClass<?> testJUnitTestGenerationUsing(String arquillianSetupCommand) throws Exception
   {

      final Result resultNewJavaClass = shellTest.execute("java-new-class --named Bean --target-package org.superbiz", 30, TimeUnit.SECONDS);
      assertThat(resultNewJavaClass, is(not(instanceOf(Failed.class))));

      final Result resultArquillianSetup = shellTest.execute(arquillianSetupCommand, 15, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      final Result createTestResult = shellTest.execute("arquillian-create-test --targets org.superbiz.Bean", 30, TimeUnit.SECONDS);

      if (createTestResult instanceof Failed)
      {
         Failed f = (Failed) createTestResult;
         f.getException().printStackTrace();
      }

      assertThat(createTestResult, is(not(instanceOf(Failed.class))));

      final DependencyBuilder junitDependency = DependencyBuilder.create("junit:junit");
      assertThat(dependencyFacet.hasDirectDependency(junitDependency), is(true));

      final DependencyBuilder universeDependency = DependencyBuilder.create("org.arquillian:arquillian-universe");
      universeDependency.setPackaging("pom");
      assertThat(dependencyFacet.hasDirectManagedDependency(universeDependency), is(true));

      final JavaClass<?> testClass = project.getFacet(JavaSourceFacet.class)
              .getTestJavaResource("org.superbiz.BeanTest")
              .getJavaType();

      assertThat(testClass.getAnnotation(RunWith.class).getLiteralValue(), is("org.jboss.arquillian.junit.Arquillian"));

      return testClass;
   }

}
