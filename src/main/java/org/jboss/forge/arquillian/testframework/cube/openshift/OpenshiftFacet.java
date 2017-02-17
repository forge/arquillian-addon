package org.jboss.forge.arquillian.testframework.cube.openshift;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;


public class OpenshiftFacet extends CubeSetupFacet {

    @Override
    public DependencyBuilder createCubeDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-openshift")
            .setPackaging("pom")
            .setScopeType("test");
    }

    @Override
    public String getQualifierForExtension() {
        return getType().toLowerCase();
    }

    @Override
    public String getType() {
        return "Openshift";
    }

    @Override
    public String getKeyForFileLocation() {
        return "definitionsFile";
    }

}
