package org.jboss.forge.arquillian.container.model;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

public enum Target {
    DOCKER(
        DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-docker")
            .setPackaging("pom")
            .setScopeType("test")),
    KUBERNETES(
        DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-kubernetes")
            .setPackaging("pom")
            .setScopeType("test")),
    OPENSHIFT(
        DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-cube-openshift")
            .setPackaging("pom")
            .setScopeType("test"));

    private DependencyBuilder dependencyBuilder;

    Target(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    public DependencyBuilder getDependencyBuilder() {
        return dependencyBuilder;
    }
}
