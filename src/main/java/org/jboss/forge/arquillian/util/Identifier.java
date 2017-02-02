package org.jboss.forge.arquillian.util;


public enum Identifier {

    TOMCAT("tomcat"),
    WILDFLY("wildfly-dist"),
    JBOSS_AS("jboss-as-dist"),
    JBOSS_EAP("jboss-as-dist"),
    WILDFLY_DOMAIN("wildfly-dist"),
    JBOSS_AS_DOMAIN("jboss-as-dist"),
    JBOSS_EAP_DOMAIN("jboss-as-dist"),
    PAYARA("payara"),
    GLASSFISH("glassfish");

    public String getArtifactID() {
        return artifactID;
    }

    private String artifactID;

    Identifier(String artifactID) {
        this.artifactID = artifactID;
    }

    public String getName() {
       return this.name().replace("_", " ").toLowerCase();
    }
}
