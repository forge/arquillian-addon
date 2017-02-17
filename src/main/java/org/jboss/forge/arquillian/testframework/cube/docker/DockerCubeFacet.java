package org.jboss.forge.arquillian.testframework.cube.docker;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;

public abstract class DockerCubeFacet extends CubeSetupFacet {


    @Override
    public DependencyBuilder createCubeDependency() {
        return DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-docker")
            .setPackaging("pom")
            .setScopeType("test");
    }

    @Override
    public String getQualifierForExtension() {
        return "docker";
    }

    @Override
    public String getKeyForFileLocation() {
        return "dockerContainersFile";
    }
}
