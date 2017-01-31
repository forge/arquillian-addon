/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.container.model;

import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // HACK fix names for JBoss AS containers since they don't follow the naming conventions
        if ("org.jboss.as".equals(getGroupId())) {
            id = id.replace("jboss-as-", "jbossas-") + "-7";
        } else if ("wildfly-dist".equals(getArtifactId()) || "tomcat".equals(getArtifactId())) {
            id = getName().toLowerCase().replaceAll("arquillian (?:container )?", "");
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
        return getDisplayName();
    }

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

    public String getNameForChameleon() {
        String artifactId = getArtifactId();
        if (artifactId.startsWith("tomcat")) {
            return artifactId;
        } else if (artifactId.startsWith("arquillian-glassfish")) {
            return "glassfish";
        } else if (artifactId.startsWith("arquillian-jbossas") || artifactId.startsWith("jboss-as")) {
            return "jboss as";
        } else if (artifactId.startsWith("wildfly")) {
            return artifactId;
        }

        return "";
    }

    public String getChameleonTarget(String version) {
        return getNameForChameleon() + ":" + version + ":" + getContainerType();
    }

    public boolean isSupportedByChameleon(String version) throws Exception {
        String containerName = this.getNameForChameleon();
        if (!containerName.isEmpty()) {
            String chameleonTarget = getChameleonTarget(version);
            System.out.println("chameleon" + chameleonTarget);
            return Target.from(chameleonTarget).isSupported();
        } else {
            System.out.println("container Name is empty");
        }

        return false;
    }

    public boolean isVersionMatches(String version) throws Exception {
        return Target.from(getChameleonTarget(version)).isVersionSupported();
    }


    // This is work aroung as method calling from chameleon is not working.

//    public boolean isVersionMatches(String version) throws Exception {
//        InputStream inputStream = Target.class.getClassLoader().getResourceAsStream("chameleon/default/containers.yaml");
//        Map<String, List<String>> map = read(inputStream);
//        if (map.get(getNameForChameleon()) != null) {
//            for (String versionExp : map.get(getNameForChameleon())) {
//                if (version.matches(versionExp)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public Map<String, List<String>> read(InputStream input) throws IOException {
//        Map<String, List<String>> map = new HashMap<>();
//        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
//            List<String> list = buffer.lines().
//                    filter(s -> s.contains("- name:") || s.contains("versionExpression:")).
//                    collect(Collectors.toList());
//            String name = null;
//            for (String s : list) {
//                if (s.contains("- name:")) {
//                    name = s.split(":")[1].trim().toLowerCase();
//                } else if (s.contains("versionExpression:") && name != null) {
//                    String version = s.split(":")[1].trim().toLowerCase();
//                    if (map.containsKey(name)) {
//                        map.get(name).add(version);
//                    } else {
//                        List<String> versionExps = new ArrayList<>();
//                        versionExps.add(version);
//                        map.put(name, versionExps);
//                    }
//                }
//            }
//        }
//        return map;
//    }
}
