/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import javax.inject.Inject;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

@RunWith(Arquillian.class)
@AddDependencies("org.assertj:assertj-core")
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class JUnitTestGenerationIntegrationTest extends ShellTestTemplate {

   @Test
   public void should_generate_junit_test_with_as_client_mode() throws Exception {

      shell().execute("java-new-class --named Bean --target-package org.superbiz")
             .execute("arquillian-setup --container-adapter glassfish-embedded --test-framework junit")
             .execute("arquillian-create-test --target-package org.superbiz --named BeanTest --targets org.superbiz.Bean --as-client ");

      assertThat(project).hasDirectDependency("junit:junit").withType("jar").withScope("test");
      assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-junit").withType("pom").withScope("test");
      assertThat(project).hasDirectManagedDependency("org.arquillian:arquillian-universe").withType("pom").withScope("import");

      final JavaClassSource testClass = extractClass(project, "org.superbiz.BeanTest");
      assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");
      assertThat(testClass).hasMethod("createDeployment").withAnnotation(Deployment.class).withEntry("testable", "false");
      assertThat(testClass).hasField("applicationUrl").ofType("java.net.URL").annotatedWith(ArquillianResource.class);
   }

   @Test
   public void should_generate_junit_test_setup() throws Exception {
      shell().execute("java-new-class --named Bean --target-package org.superbiz")
              .execute("arquillian-setup --container-adapter glassfish-embedded --test-framework junit")
              .execute("arquillian-create-test --target-package org.superbiz --named BeanTest --targets org.superbiz.Bean");

      assertThat(project).hasDirectDependency("junit:junit").withType("jar").withScope("test");
      assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-junit").withType("pom").withScope("test");
      assertThat(project).hasDirectManagedDependency("org.arquillian:arquillian-universe").withType("pom").withScope("import");

      final JavaClassSource testClass = extractClass(project, "org.superbiz.BeanTest");
      assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");
      assertThat(testClass).hasMethod("createDeployment").withAnnotation(Deployment.class).withEntry("testable", null);
      assertThat(testClass).hasField("bean").ofType("org.superbiz.Bean").annotatedWith(Inject.class);
   }

   @Test
   public void should_generate_junit_test_setup_in_standalone_mode() throws Exception {
      shell().execute("java-new-class --named Bean --target-package org.superbiz")
              .execute("arquillian-setup  --standalone --test-framework junit")
              .execute("arquillian-create-test --target-package org.superbiz --named BeanTest");

      assertThat(project).hasDirectDependency("junit:junit").withType("jar").withScope("test");
      assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-junit-standalone").withType("pom").withScope("test");
      assertThat(project).hasDirectManagedDependency("org.arquillian:arquillian-universe").withType("pom").withScope("import");

      final JavaClassSource testClass = extractClass(project, "org.superbiz.BeanTest");
      assertThat(testClass).hasAnnotation(RunWith.class).withValue("org.jboss.arquillian.junit.Arquillian");
      assertThat(testClass).doesNotHaveMethod("createDeployment");
      assertThat(testClass).doesNotHaveField("bean");
   }

}
