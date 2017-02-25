/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.integration;

import org.apache.maven.model.Profile;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.integration.extension.AddDependencies;
import test.integration.extension.AddPackage;
import test.integration.support.ShellTestTemplate;

import java.util.concurrent.TimeoutException;

import static test.integration.support.assertions.ForgeAssertions.assertThat;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
@RunWith(Arquillian.class)
@AddPackage(ShellTestTemplate.PACKAGE_NAME)
public class ContainerInstallationIntegrationTest extends ShellTestTemplate {

    @Test
    public void should_install_open_ejb_container() throws Exception {
        installContainerAssertProfileAndDependencies("openejb-embedded-3.1",
            "org.jboss.arquillian.container:arquillian-openejb-embedded-3.1",
            "org.apache.openejb:openejb-core");

    }

    @Test
    public void should_install_open_web_beans_container() throws Exception {
        installContainerAssertProfileAndDependencies("openwebbeans-embedded-1",
            "org.jboss.arquillian.container:arquillian-openwebbeans-embedded-1",
            "org.apache.openwebbeans:openwebbeans-impl");
    }

    @Test
    public void should_install_glass_fish_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("glassfish-embedded");
    }

    @Test
    public void should_install_glassfish_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("glassfish-managed");
    }

    @Test
    public void should_install_glassfish_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("glassfish-remote");
    }

    @Test
    public void should_install_payara_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("payara-embedded");
    }

    @Test
    public void should_install_payara_managed_ontainer() throws Exception {
        installContainerAssertProfileAndDependencies("payara-managed");
    }

    @Test
    public void should_install_payara_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("payara-remote");
    }

    @Test
    public void should_install_jboss_as_5_1_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-managed-5.1",
            "org.jboss.arquillian.container:arquillian-jbossas-managed-5.1");
    }

    @Test
    public void should_install_jboss_as_5_1_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-remote-5.1",
            "org.jboss.arquillian.container:arquillian-jbossas-remote-5.1");
    }

    @Test
    public void should_install_jboss_as_5_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-remote-5",
            "org.jboss.arquillian.container:arquillian-jbossas-remote-5");
    }

    @Test
    public void should_install_jboss_as_6_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-embedded-6",
            "org.jboss.arquillian.container:arquillian-jbossas-embedded-6");
    }

    @Test
    public void should_install_jboss_as_6_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-managed-6",
            "org.jboss.arquillian.container:arquillian-jbossas-managed-6");
    }

    @Test
    public void should_install_jboss_as_6_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("jbossas-remote-6",
            "org.jboss.arquillian.container:arquillian-jbossas-remote-6");
    }

    @Test
    public void should_install_jboss_7_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-as-managed-7");
    }

    @Test
    public void should_install_jboss_as_7_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-as-remote-7");
    }

    @Test
    public void should_install_jboss_eap_7_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-eap-embedded-7");
    }

    @Test
    public void should_install_jboss_eap_7_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-eap-managed-7");
    }

    @Test
    public void should_install_jboss_eap_7_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-eap-remote-7");
    }

    @Test
    public void should_install_jboss_as_7_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("jboss-as-embedded-7");
    }


    @Test
    public void should_install_wildfly_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("wildfly-managed");
    }

    @Test
    public void should_install_wildfly_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("wildfly-remote");
    }

    @Test
    public void should_install_wildfly_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("wildfly-embedded");
    }

    @Test
    public void should_install_jetty_6_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("jetty-embedded-6.1",
            "org.jboss.arquillian.container:arquillian-jetty-embedded-6.1",
            "org.mortbay.jetty:jetty");
    }

    @Test
    public void should_install_jetty_7_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("jetty-embedded-7",
            "org.jboss.arquillian.container:arquillian-jetty-embedded-7",
            "org.eclipse.jetty:jetty-webapp");
    }

    @Test
    public void should_install_tomcat_6_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("tomcat-embedded-6",
            "org.jboss.arquillian.container:arquillian-tomcat-embedded-6",
            "org.apache.tomcat:catalina",
            "org.apache.tomcat:coyote",
            "org.apache.tomcat:jasper");
    }

    @Test
    @Ignore("Not in default maven repo")
    public void should_install_was_7_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("was-remote-7",
            "org.jboss.arquillian.container:arquillian-was-remote-7");
    }

    @Test
    @Ignore("Not in default maven repo")
    public void should_install_was_8_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("was-embedded-8",
            "org.jboss.arquillian.container:arquillian-was-embedded-8");
    }

    @Test
    @Ignore("Not in default maven repo")
    public void should_install_was_8_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("was-remote-8",
            "org.jboss.arquillian.container:arquillian-was-remote-8");
    }

    @Test
    public void should_install_tomcat_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("tomcat-remote");

    }

    @Test
    public void should_install_tomcat_managed_container() throws Exception {
        installContainerAssertProfileAndDependencies("tomcat-managed");

    }

    @Test
    public void should_install_weld_ee_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("weld-ee-embedded-1.1",
            "org.jboss.arquillian.container:arquillian-weld-ee-embedded-1.1");
    }

    @Test
    public void should_install_weld_se_embedded_container() throws Exception {
        installContainerAssertProfileAndDependencies("weld-se-embedded-1",
            "org.jboss.arquillian.container:arquillian-weld-se-embedded-1");
    }

    @Test
    public void should_install_weld_se_embedded_1_1_container() throws Exception {
        installContainerAssertProfileAndDependencies("weld-se-embedded-1.1",
            "org.jboss.arquillian.container:arquillian-weld-se-embedded-1.1");
    }

    @Test
    public void should_install_weblogic_remote_container() throws Exception {
        installContainerAssertProfileAndDependencies("wls-remote-10.3",
            "org.jboss.arquillian.container:arquillian-wls-remote-10.3");
    }

    private void installContainerAssertProfileAndDependencies(final String container, String... dependencies) throws Exception {
        executeCmd(container);

        final Profile profile = getProfile();
        final String profileId = "arquillian-" + container;

        assertThat(profile).hasId(profileId);


        for (String dependency : dependencies) {
            String[] gav = dependency.split(":");
            assertThat(profile).hasDependency(dependency).withGroupId(gav[0]).withArtifactId(gav[1]);
        }

        assertJunitAndUniverseDependency();

        assertContainerOrPropertyFromArquillianConfig("<container qualifier=\"" + profileId + "\"/>");
    }

    private void installContainerAssertProfileAndDependencies(final String container) throws TimeoutException {
        executeCmd(container);

        Profile profile = getProfile();
        final String profileId = "arquillian-" + container;

        assertThat(profile).hasId(profileId);
        assertJunitAndUniverseDependency();

        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-chameleon").withType("pom").withScope("test");
        assertContainerOrPropertyFromArquillianConfig(
            "<container default=\"true\" qualifier=\"" + profileId + "\">",
            "<property name=\"chameleonTarget\">${chameleon.target}</property>");
    }

    private void executeCmd(String container) throws TimeoutException {
        shell().execute("arquillian-setup --container-adapter " + container + " --test-framework junit");
    }

    private Profile getProfile() {
        MavenFacet mavenFacet = project.getFacet(MavenFacet.class);

        return mavenFacet.getModel().getProfiles().get(0);
    }

    private void assertContainerOrPropertyFromArquillianConfig(String... properties) {
        final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
        final FileResource<?> arquillianXML = facet.getTestResource("arquillian.xml");

        assertThat(arquillianXML.getContents()).contains(properties);
    }

    private void assertJunitAndUniverseDependency() {
        assertThat(project).hasDirectDependency("junit:junit").withType("jar").withScope("test");
        assertThat(project).hasDirectDependency("org.arquillian.universe:arquillian-junit").withType("pom").withScope("test");
        assertThat(project).hasDirectManagedDependency("org.arquillian:arquillian-universe").withType("pom").withScope("import");
    }
}
