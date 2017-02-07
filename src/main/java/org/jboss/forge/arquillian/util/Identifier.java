package org.jboss.forge.arquillian.util;


import org.jboss.forge.arquillian.container.model.Container;

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

    private static final String AS = " AS ";
    private static final String DOMAIN = "Domain";
    private static final String EAP = " EAP ";
    private static final String EAP_DOMAIN = EAP + DOMAIN;
    private static final String AS_DOMAIN = AS + DOMAIN;
    private String artifactID;

    Identifier(String artifactID) {
        this.artifactID = artifactID;
    }

    public static String getNameForChameleon(Container container) {
        final String artifactId = container.getArtifactId();
        final String name = container.getName();
        if (Identifier.TOMCAT.getArtifactID().equals(artifactId)) {
            return Identifier.TOMCAT.getName();
        } else if (Identifier.JBOSS_AS.getArtifactID().equals(artifactId) && name.contains(AS) && !name.contains(AS_DOMAIN)) {
            return Identifier.JBOSS_AS.getName();
        } else if (Identifier.JBOSS_AS_DOMAIN.getArtifactID().equals(artifactId) && name.contains(AS_DOMAIN)) {
            return Identifier.JBOSS_AS_DOMAIN.getName();
        } else if (Identifier.JBOSS_EAP_DOMAIN.getArtifactID().equals(artifactId) && name.contains(EAP_DOMAIN)) {
            return Identifier.JBOSS_EAP_DOMAIN.getName();
        } else if (Identifier.JBOSS_EAP.getArtifactID().equals(artifactId) && name.contains(EAP) && !name.contains(EAP_DOMAIN)) {
            return Identifier.JBOSS_EAP.getName();
        } else if (Identifier.WILDFLY_DOMAIN.getArtifactID().equals(artifactId) && name.contains(DOMAIN)) {
            return Identifier.WILDFLY_DOMAIN.getName();
        } else if (Identifier.WILDFLY.getArtifactID().equals(artifactId) && !name.contains(DOMAIN)) {
            return Identifier.WILDFLY.getName();
        } else if (Identifier.GLASSFISH.getArtifactID().equals(artifactId)) {
            return Identifier.GLASSFISH.getName();
        } else if (Identifier.PAYARA.getArtifactID().equals(artifactId)) {
            return Identifier.PAYARA.getName();
        }

        return "";
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getName() {
        return this.name().replace("_", " ").toLowerCase();
    }
}
