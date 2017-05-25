package org.jboss.forge.arquillian.container.provider;

import org.assertj.core.api.JUnitSoftAssertions;
import org.jboss.forge.arquillian.container.model.Container;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class ContainerDirectoryParserTest {

    private ContainerDirectoryParser containerDirectoryParser;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void loadContainers() throws IOException {
        containerDirectoryParser = new ContainerDirectoryParser();
        containerDirectoryParser.containerDirectoryLocationProvider = new FileContainerIndexLocationProvider();
    }

    @Test
    public void should_assigned_group_id_and_artifact_id_from_chameleon() throws IOException {
        List<Container> containers = containerDirectoryParser.getContainers();

        containers.forEach(container -> {
            softly.assertThat(container.getGroupId()).isNotNull();
            softly.assertThat(container.getArtifactId()).isNotNull();
        });
    }

    @Test
    public void should_assigned_group_id_and_artifact_id_for_tomcat() throws IOException {
        final List<Container> containers = containerDirectoryParser.getContainers();
        final Container container = getContainer("Arquillian Container Tomcat Remote", containers);

        softly.assertThat(container.getGroupId()).isEqualTo("org.apache.tomcat");
        softly.assertThat(container.getArtifactId()).isEqualTo("tomcat");
    }

    @Test
    public void should_assigned_group_id_and_artifact_id_for_wildfly_managed() throws IOException {
        final List<Container> containers = containerDirectoryParser.getContainers();
        final Container container = getContainer("Arquillian Container WildFly Managed", containers);

        softly.assertThat(container.getGroupId()).isEqualTo("org.wildfly");
        softly.assertThat(container.getArtifactId()).isEqualTo("wildfly-dist");
    }

    @Test
    public void should_assigned_group_id_and_artifact_id_for_jboss_as_managed_7() throws IOException {
        final List<Container> containers = containerDirectoryParser.getContainers();
        final Container container = getContainer("Arquillian Container JBoss AS Managed 7.x", containers);

        softly.assertThat(container.getGroupId()).isEqualTo("org.jboss.as");
        softly.assertThat(container.getArtifactId()).isEqualTo("jboss-as-dist");
    }

    @Test
    public void should_assigned_group_id_and_artifact_id_for_payara_managed() throws IOException {
        final List<Container> containers = containerDirectoryParser.getContainers();
        final Container container = getContainer("Arquillian Container Payara Managed", containers);

        softly.assertThat(container.getGroupId()).isEqualTo("fish.payara.distributions");
        softly.assertThat(container.getArtifactId()).isEqualTo("payara");
    }

    private Container getContainer(String name, List<Container> containers) {
        final Optional<Container> container = containers.stream().filter(containerPredicate -> containerPredicate.getName().equals(name))
            .findFirst();

        if (container.isPresent()) {
            return container.get();
        } else {
            throw new IllegalStateException("No container found with name" + name);
        }
    }
}
