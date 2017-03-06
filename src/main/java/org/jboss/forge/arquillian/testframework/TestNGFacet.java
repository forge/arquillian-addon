/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.testframework;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.core.TestFrameworkFacet;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class TestNGFacet extends TestFrameworkFacet {
    @Override
    public String getFrameworkName() {
        return "testng";
    }

    @Override
    public String getTemplateLocation() {
        return "/TestNGTest.ftl";
    }

    @Override
    public String getTemplateStandaloneLocation() {
        return "/TestNGStandaloneTest.ftl";
    }

    @Override
    public String getVersionPropertyName() {
        return "version.testng";
    }


    @Override
    public DependencyBuilder createFrameworkDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.testng")
            .setArtifactId("testng")
            .setScopeType("test");
    }

    @Override
    public DependencyBuilder createArquillianDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-testng")
            .setScopeType("test")
            .setPackaging("pom");
    }

    @Override
    public DependencyBuilder createArquillianStandaloneDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-testng-standalone")
            .setScopeType("test")
            .setPackaging("pom");
    }
}
