package org.jboss.forge.arquillian.container.model;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

public enum CubeConfiguration {

    DOCKER(Target.DOCKER.getDependencyBuilder(), "docker", "Docker", "dockerContainers"),
    DOCKER_COMPOSE(Target.DOCKER.getDependencyBuilder(), "docker", "Docker Compose", "dockerContainersFile"),
    KUBERNETES(Target.KUBERNETES.getDependencyBuilder(), "kubernetes", "Kubernetes", "env.config.url"),
    OPENSHIFT(Target.OPENSHIFT.getDependencyBuilder(), "openshift", "Openshift", "definitionsFile");

    CubeConfiguration(DependencyBuilder dependency, String qualifierForExtension, String type, String keyForFileLocation) {
        this.dependency = dependency;
        this.qualifierForExtension = qualifierForExtension;
        this.type = type;
        this.keyForFileLocation = keyForFileLocation;
    }

    public DependencyBuilder getDependency() {
        return dependency;
    }

    public String getQualifierForExtension() {
        return qualifierForExtension;
    }

    public String getType() {
        return type;
    }

    public String getKeyForFileLocation() {
        return keyForFileLocation;
    }

    private DependencyBuilder dependency;

    private String qualifierForExtension;

    private String type;

    private String keyForFileLocation;

}
