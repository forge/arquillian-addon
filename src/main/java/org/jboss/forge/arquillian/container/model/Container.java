/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.container.model;

import org.arquillian.container.chameleon.spi.model.Dist;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
public class Container implements Comparable<Container> {

    private static final Map<String, String> ABBREVIATIONS = new HashMap<>();

    static {
        ABBREVIATIONS.put("jbossas-", "jboss-as-");
        ABBREVIATIONS.put("wls-", "weblogic-server-");
        ABBREVIATIONS.put("was-", "websphere-as-");
    }

    private String groupId;
    private String artifactId;
    private String name;
    private ContainerType containerType;
    private List<Dependency> dependencies;
    private Dependency download;
    private List<Configuration> configurations;

    public static String idForDisplayName(String displayName) {
        return abbr(displayName.replaceAll("_", "-").toLowerCase());
    }

    public static String expandAbbr(String id) {
        for (Map.Entry<String, String> abbr : ABBREVIATIONS.entrySet()) {
            if (id.contains(abbr.getKey())) {
                id = id.replace(abbr.getKey(), abbr.getValue());
            }
        }

        return id;
    }

    public static String abbr(String id) {
        for (Map.Entry<String, String> abbr : ABBREVIATIONS.entrySet()) {
            if (id.contains(abbr.getValue())) {
                id = id.replace(abbr.getValue(), abbr.getKey());
            }
        }

        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Dependency getDownload() {
        return download;
    }

    public void setDownload(Dependency download) {
        this.download = download;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }

    public String getId() {
        return getBaseId();
    }

    public String getDisplayName() {
        return expandAbbr(getBaseId()).replaceAll("-", "_").toUpperCase();
    }

    public String getProfileId() {
        return "arquillian-" + getBaseId();
    }

    private String getBaseId() {
        String id = getArtifactId().replaceAll("arquillian-(?:container-)?", "");

        // HACK display names for CLI depending on name for containers who is installing
        // using chameleon & if it doesn't have unique artifact ID.

        if (Identifier.WILDFLY.getArtifactID().equals(getArtifactId()) ||
            Identifier.TOMCAT.getArtifactID().equals(getArtifactId()) ||
            Identifier.GLASSFISH.getArtifactID().equals(getArtifactId()) ||
            Identifier.PAYARA.getArtifactID().equals(getArtifactId())) {
            id = getName().toLowerCase().replaceAll("arquillian (?:container )?", "");
            id = id.replaceAll(" ", "-");
        } else if (Identifier.JBOSS_AS.getArtifactID().equals(getArtifactId())) {
            id = getName().toLowerCase().replaceAll("\\b(?:arquillian |container |.x)\\b", "");
            id = id.replaceAll(" ", "-");
        }

        return id;
    }

    public int compareTo(Container other) {
        return getId().compareTo(other.getId());
    }

    public DependencyBuilder asDependency() {
        return DependencyBuilder.create()
            .setGroupId(getGroupId())
            .setArtifactId(getArtifactId());
    }

    @Override
    public String toString() {
        final String name = getName().toLowerCase().replaceAll("arquillian (?:container )?", "");

        return name.replace(" ", "_").toUpperCase();

    }

    public boolean shouldIncludeDirectDependency() {
        final String displayName = this.getDisplayName();

        return displayName.equals("PAYARA_EMBEDDED") || displayName.equals("GLASSFISH_EMBEDDED");
    }

    public String getChameleonTarget(String version) {
        return Identifier.getNameForChameleon(this) + ":" + version + ":" + getContainerType();
    }

    public boolean isSupportedByChameleon(String version) throws Exception {
        // We have issue with chameleon for Payara & Glassfish - Embedded for classloading
        // as it's running in the same JVM.

        final boolean isProblemWithChameleon = shouldIncludeDirectDependency();
        if (isProblemWithChameleon) {
            return !isProblemWithChameleon;
        }

        String containerName = Identifier.getNameForChameleon(this);
        if (!containerName.isEmpty()) {
            String chameleonTarget = getChameleonTarget(version);
            return Target.from(chameleonTarget).isSupported();
        }

        return false;
    }

    public void setGroupIdAndArtifactIdFromChameleonConfiguration(org.arquillian.container.chameleon.spi.model.Container... containers) throws Exception {
        String pattern = "(?<=arquillian container).*(?=remote|managed|embedded)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(this.getName().toLowerCase());

        if (m.find()) {
            Optional<String> containerName = Optional.ofNullable(m.group(0));
            if (containerName.isPresent()) {
                String finalContainerName = containerName.get().trim();
                Arrays.stream(containers)
                    .filter(container -> {
                        final boolean matched = Arrays.stream(container.getAdapters())
                            .anyMatch(adapter -> adapter.getType().equalsIgnoreCase(this.containerType.toString()));

                        return matched && container.getName().equalsIgnoreCase(finalContainerName);
                    })
                    .forEach(container -> {
                        final Dist dist = container.getDist();
                        final String[] split = dist.coordinates().split(":");
                        if (split.length >= 2) {
                            this.setGroupId(split[0]);
                            this.setArtifactId(split[1]);
                        } else {
                            throw new IllegalStateException("Group Id or Artifact Id is missing in distribution of chameleon configuration for container: " + container.getName());
                        }
                    });
            }
        }
    }


    public boolean isVersionMatches(String version) throws Exception {
        final String chameleonTarget = getChameleonTarget(version);

        return Target.from(chameleonTarget).isVersionSupported();
    }
}
