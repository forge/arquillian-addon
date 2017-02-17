package org.jboss.forge.arquillian.testframework.cube.kubernetes;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;

public class KubernetesFacet extends CubeSetupFacet {

    @Override
    public DependencyBuilder createCubeDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-kubernetes")
            .setPackaging("pom")
            .setScopeType("test");
    }

    @Override
    public String getQualifierForExtension() {
        return getType().toLowerCase();
    }

    @Override
    public String getType() {
        return "Kubernetes";
    }

    @Override
    public String getKeyForFileLocation() {
        return "env.config.url";
    }

}
