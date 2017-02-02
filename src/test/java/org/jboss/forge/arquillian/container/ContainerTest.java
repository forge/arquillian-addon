/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.container;

import org.jboss.forge.arquillian.container.model.Container;
import org.jboss.forge.arquillian.container.model.ContainerType;
import org.jboss.forge.arquillian.util.Identifier;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
public class ContainerTest {

    private static final String JBOSS_EAP_7_REMOTE = "Arquillian Container JBoss EAP Remote 7.x";
    private static final String JBOSS_AS_7_REMOTE = "Arquillian Container JBoss AS Remote 7.x";
    private static final String FOO = "foo";
    private static final String JBOSS_AS_7_DOMAIN_REMOTE = "Arquillian Container JBoss AS Domain Remote 7.x";

    @Test
    public void should_have_id_for_tomcat_embedded_6() {
        final Container container = createContainer("tomcat-embedded-6",
                "Arquillian Container Tomcat Embedded 6.x");

        assertThat(container.getId()).isEqualTo("tomcat-embedded-6");
    }

    @Test
    public void should_have_profile_id_for_glassfish_remote() {
        final Container container = createContainer(Identifier.GLASSFISH.getArtifactID(),
                "Arquillian Container GlassFish Remote");

        assertThat(container.getProfileId()).isEqualTo("arquillian-glassfish-remote");
    }

    @Test
    public void should_have_id_and_chameleon_name_for_tomcat_remote() {
        final Container container = createContainer(Identifier.TOMCAT.getArtifactID(),
                "Arquillian Container Tomcat Remote");

        assertThat(container.getId()).isEqualTo("tomcat-remote");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.TOMCAT.getName());
    }

    @Test
    public void should_have_id_and_chameleon_name_for_payara_embedded() {
        final Container container = createContainer(Identifier.PAYARA.getArtifactID(),
                "Arquillian Container Payara Embedded");

        assertThat(container.getId()).isEqualTo("payara-embedded");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.PAYARA.getName());

    }

    @Test
    public void should_have_id_and_chameleon_name_for_glassfish_managed() {
        final Container container = createContainer(Identifier.GLASSFISH.getArtifactID(),
                "Arquillian Container Glassfish Managed");

        assertThat(container.getId()).isEqualTo("glassfish-managed");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.GLASSFISH.getName());

    }

    @Test
    public void should_have_id_and_chameleon_name_for_wildfly_remote() {
        final Container container = createContainer(Identifier.WILDFLY.getArtifactID(),
                "Arquillian Container Wildfly Remote");

        assertThat(container.getId()).isEqualTo("wildfly-remote");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.WILDFLY.getName());
    }

    @Test
    public void should_have_id_and_chameleon_name_for_wildfly_domain_managed() {
        final Container container = createContainer(Identifier.WILDFLY.getArtifactID(),
                "Arquillian Container Wildfly Domain Managed");

        assertThat(container.getId()).isEqualTo("wildfly-domain-managed");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase("Wildfly Domain");
    }

    @Test
    public void should_have_id_and_chameleon_name_for_jboss_eap_remote_7() {
        final Container container = createContainer(Identifier.JBOSS_EAP.getArtifactID(),
                JBOSS_EAP_7_REMOTE);

        assertThat(container.getId()).isEqualTo("jboss-eap-remote-7");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase("jboss eap");
    }

    @Test
    public void should_have_id_for_jboss_eap_domain_managed_7() {
        final Container container = createContainer(Identifier.JBOSS_EAP_DOMAIN.getArtifactID(),
                "Arquillian Container JBoss EAP Domain Managed 7.x");

        assertThat(container.getId()).isEqualTo("jboss-eap-domain-managed-7");
        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase("jboss eap domain");
    }

    @Test
    public void should_have_id_and_chameleon_name_for_jboss_eap_7() {
        final Container container = createContainer(Identifier.JBOSS_EAP.getArtifactID(),
                JBOSS_EAP_7_REMOTE);

        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.JBOSS_EAP.getName());
        assertThat(container.getId()).isEqualTo("jboss-eap-remote-7");
    }

    @Test
    public void should_have_id_and_chameleon_name_for_jboss_as_7() {
        final Container container = createContainer(Identifier.JBOSS_AS.getArtifactID(),
                JBOSS_AS_7_REMOTE);

        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase(Identifier.JBOSS_AS.getName());
        assertThat(container.getId()).isEqualTo("jboss-as-remote-7");
    }

    @Test
    public void should_have_id_and_chameleon_name_for_jboss_as_domain_7() {
        final Container container = createContainer(Identifier.JBOSS_AS.getArtifactID(),
                JBOSS_AS_7_DOMAIN_REMOTE);

        assertThat(container.getNameForChameleon()).isEqualToIgnoringCase("JBoss AS Domain");
        assertThat(container.getId()).isEqualTo("jboss-as-domain-remote-7");
    }

    @Test
    public void should_support_by_chameleon_for_jboss_as_7() throws Exception {
        final Container container = createContainerWithType(Identifier.JBOSS_AS.getArtifactID(),
                JBOSS_AS_7_REMOTE, ContainerType.MANAGED);

        final String version = "7.2.0.Final";

        assertThat(container.isSupportedByChameleon(version)).isTrue();
        assertThat(container.isVersionMatches(version)).isTrue();
    }

    @Test
    public void should_support_by_chameleon_for_jboss_as_domain_7() throws Exception {
        final Container container = createContainerWithType(Identifier.JBOSS_AS.getArtifactID(),
                JBOSS_AS_7_DOMAIN_REMOTE, ContainerType.MANAGED);

        final String version = "7.2.0.Final";

        assertThat(container.isSupportedByChameleon(version)).isTrue();
        assertThat(container.isVersionMatches(version)).isTrue();
    }

    @Test
    public void should_not_support_by_chameleon_for_jboss_as_7() throws Exception {
        final Container container = createContainerWithType(Identifier.JBOSS_AS.getArtifactID(),
                JBOSS_AS_7_REMOTE, ContainerType.MANAGED);

        final String version = FOO;

        assertThat(container.isSupportedByChameleon(version)).isFalse();
        assertThat(container.isVersionMatches(version)).isFalse();
    }

    @Test
    public void should_not_support_by_chameleon_for_false_name() throws Exception {
        final Container container = createContainerWithType("junit",
                "Arquillian Container Junit", ContainerType.MANAGED);

        final String version = FOO;

        assertThat(container.isSupportedByChameleon(version)).isFalse();
        assertThat(container.isVersionMatches(version)).isFalse();
    }

    private Container createContainerWithArtifactId(String artifactId) {
        Container container = new Container();
        container.setArtifactId(artifactId);

        return container;
    }

    private Container createContainerWithType(String artifactId, String name, ContainerType containerType) {
        Container container = createContainer(artifactId, name);
        container.setContainerType(containerType);

        return container;
    }

    private Container createContainer(String artifactId, String name) {
        Container container = createContainerWithArtifactId(artifactId);
        container.setName(name);

        return container;
    }

}
