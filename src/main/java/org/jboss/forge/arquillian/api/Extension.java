package org.jboss.forge.arquillian.api;

import org.jboss.forge.addon.dependencies.Dependency;

public class Extension {

    private final String name;
    private final Dependency dependency;
    
    public Extension(Dependency dependency) {
        this(dependency.getCoordinate().getArtifactId(), dependency);
    }

    public Extension(String name, Dependency dependency) {
       this.name = name;
       this.dependency = dependency;
    }
    
    public String getName() {
        return name;
    }
    
    public Dependency getDependency() {
        return dependency;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Extension)) {
            return false;
        }
        Extension other = (Extension) obj;
        return isDependency(other.getDependency());
    }
    
    public boolean isDependency(Dependency other) {
        return this.getDependency().getCoordinate().getGroupId().equals(
                other.getCoordinate().getGroupId()) && 
                this.getDependency().getCoordinate().getArtifactId().equals(
                other.getCoordinate().getArtifactId());
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
