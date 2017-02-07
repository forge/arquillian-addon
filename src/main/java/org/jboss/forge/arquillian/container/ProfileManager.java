package org.jboss.forge.arquillian.container;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.arquillian.container.model.Container;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileManager {

    @Inject
    private ContainerResolver containerResolver;

    public List<String> getArquillianProfiles(Project project) {
        MavenFacet mavenCoreFacet = project.getFacet(MavenFacet.class);
        List<String> profiles = new ArrayList<>();
        List<Profile> profileList = mavenCoreFacet.getModel().getProfiles();
        for (Profile profile : profileList) {
            profiles.add(profile.getId());
        }
        Collections.sort(profiles);
        return profiles;
    }

    public void addProfile(Project project, Container container, List<Dependency> dependencies) {
        Dependency[] deps = new Dependency[dependencies.size()];
        addProfile(project, container, dependencies.toArray(deps));
    }

    public void addProfile(Project project, Container container, String chameleonTargetVersion) {
        MavenFacet facet = project.getFacet(MavenFacet.class);

        Profile profile = createProfile(container);

        addBuildBaseToProfile(profile, container, chameleonTargetVersion);

        Model pom = checkForExistingProfileAndGetPom(facet, container, profile);
        facet.setModel(pom);
    }

    public void addProfile(Project project, Container container, Dependency... dependencies) {
        MavenFacet facet = project.getFacet(MavenFacet.class);

        Profile profile = createProfile(container);

        addBuildBaseToProfile(profile, container, null);
        addDependencyToProfile(profile, dependencies);

        Model pom = checkForExistingProfileAndGetPom(facet, container, profile);
        facet.setModel(pom);
    }

    private Model checkForExistingProfileAndGetPom(MavenFacet facet, Container container, Profile profile) {
        Model pom = facet.getModel();

        Profile existingProfile = findProfileById(container.getProfileId(), pom);

        if (existingProfile != null) {
            // preserve existing id
            profile.setId(existingProfile.getId());
            pom.removeProfile(existingProfile);
        }

        pom.addProfile(profile);

        return pom;
    }

    private void addBuildBaseToProfile(Profile profile, Container container, String containerVersion) {
        BuildBase buildBase = new BuildBase();

        Plugin surefirePlugin = createSurefirePlugin();
        String profileId = container.getProfileId();

        if (containerVersion != null) {
            surefirePlugin.setConfiguration(buildConfiguration(profileId, container.getChameleonTarget(containerVersion)));
        } else {
            surefirePlugin.setConfiguration(buildConfiguration(profileId));
        }

        buildBase.addPlugin(surefirePlugin);

        profile.setBuild(buildBase);
    }

    private void addDependencyToProfile(Profile profile, Dependency... dependencies) {
        for (Dependency dependency : dependencies) {
            profile.addDependency(new MavenDependencyAdapter(DependencyBuilder.create(dependency)));
        }
    }

    private Profile createProfile(Container container) {
        Profile profile = new Profile();
        profile.setId(container.getProfileId());

        return profile;
    }

    private Plugin createSurefirePlugin() {
        Plugin surefirePlugin = new Plugin();
        surefirePlugin.setArtifactId("maven-surefire-plugin");
        surefirePlugin.setVersion("2.14.1");

        return surefirePlugin;

    }

    public Container getContainer(String profile) {
        String profileId = profile.replaceFirst("^arq-", "arquillian-");
        for (Container container : containerResolver.getContainers()) {
            if (container.getProfileId().equals(profileId)) {
                return container;
            }
        }
        throw new RuntimeException("Container not found for profile " + profile);
    }

    private Profile findProfileById(String profileId, Model pom) {
        for (Profile profile : pom.getProfiles()) {
            if (profileId.equalsIgnoreCase(profile.getId().replaceFirst("^arq-", "arquillian-"))) {
                return profile;
            }
        }
        return null;
    }

    /*
     * Create the surefire plugin configuration, so we call the relevant Arquillian container config
     *
     * <plugin> <artifactId>maven-surefire-plugin</artifactId> <configuration> <systemPropertyVariables>
     * <arquillian.launch>${profileId}</arquillian.launch> </systemPropertyVariables> </configuration> </plugin>
     */
    private Object buildConfiguration(String profileId) {
        try {
            return Xpp3DomBuilder.build(new StringReader(
                "<configuration>\n" +
                    "    <systemPropertyVariables>\n" +
                    "        <arquillian.launch>" + profileId + "</arquillian.launch>\n" +
                    "    </systemPropertyVariables>\n" +
                    "</configuration>"));
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
 * Create the surefire plugin configuration, so we call the relevant Arquillian container config
 *
 * <plugin> <artifactId>maven-surefire-plugin</artifactId> <configuration> <systemPropertyVariables>
 * <arquillian.launch>${profileId}</arquillian.launch> <chameleon.target> ${chameleonTarget}</chameleon.target></systemPropertyVariables> </configuration> </plugin>
 */
    private Object buildConfiguration(String profileId, String chameleonTarget) {
        try {
            return Xpp3DomBuilder.build(new StringReader(
                "<configuration>\n" +
                    "    <systemPropertyVariables>\n" +
                    "        <arquillian.launch>" + profileId + "</arquillian.launch>\n" +
                    "        <chameleon.target>" + chameleonTarget + "</chameleon.target>\n" +
                    "    </systemPropertyVariables>\n" +
                    "</configuration>"));
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
