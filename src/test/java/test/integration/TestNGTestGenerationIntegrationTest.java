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
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@RunWith(Arquillian.class)
public class TestNGTestGenerationIntegrationTest 
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
   public void shouldGenerateTestNGBasedTest() throws Exception
   {
      testNgTestGenerationUsing("arquillian-setup --container-adapter glassfish-embedded-3.1 --test-framework testng");
   }

   private void testNgTestGenerationUsing(String arquillianSetupCommand) throws Exception {

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

      shellTest.getShell().setCurrentResource(project.getRoot());

      final Result resultNewJavaClass = shellTest.execute("java-new-class --named Bean --target-package org.superbiz", 15, TimeUnit.SECONDS);
      assertThat(resultNewJavaClass, is(not(instanceOf(Failed.class))));

      final Result resultArquillianSetup = shellTest.execute(arquillianSetupCommand, 5, TimeUnit.SECONDS);
      assertThat(resultArquillianSetup, is(not(instanceOf(Failed.class))));

      final Result createTestResult = shellTest.execute("arquillian-create-test --class org.superbiz.Bean", 15, TimeUnit.SECONDS);
      assertThat(createTestResult, is(not(instanceOf(Failed.class))));

      final DependencyBuilder junitDependency = DependencyBuilder.create("org.testng:testng");
      assertThat(dependencyFacet.hasDirectDependency(junitDependency), is(true));

      final DependencyBuilder universeJunitDependency = DependencyBuilder.create("org.arquillian.universe:arquillian-testng");
      universeJunitDependency.setPackaging("pom");
      assertThat(dependencyFacet.hasDirectDependency(universeJunitDependency), is(true));

      final DependencyBuilder universeDependency = DependencyBuilder.create("org.arquillian:arquillian-universe");
      universeDependency.setPackaging("pom");
      assertThat(dependencyFacet.hasDirectManagedDependency(universeDependency), is(true));

      final JavaSource<?> testClass = project.getFacet(JavaSourceFacet.class)
              .getTestJavaResource("org.superbiz.BeanTest")
              .getJavaType();

      assertThat(testClass.hasImport("org.jboss.arquillian.testng.Arquillian"), is(true));
   }
}
