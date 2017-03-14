/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.api.core.testframework;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

/**
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class JUnitFacet extends TestFrameworkFacet {
    @Override
    public String getFrameworkName() {
        return "junit";
    }

    @Override
    public String getTemplateLocation() {
        return "/JUnitTest.ftl";
    }

    @Override
    public String getTemplateStandaloneLocation() {
        return "/JUnitStandaloneTest.ftl";
    }

    @Override
    public String getVersionPropertyName() {
        return "version.junit";
    }

    @Override
    public DependencyBuilder createFrameworkDependency() {
        return DependencyBuilder.create()
            .setGroupId("junit")
            .setArtifactId("junit")
            .setScopeType("test");
    }

    @Override
    public DependencyBuilder createArquillianDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-junit")
            .setScopeType("test")
            .setPackaging("pom");
    }

    @Override
    public DependencyBuilder createArquillianStandaloneDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-junit-standalone")
            .setScopeType("test")
            .setPackaging("pom");
    }
}
