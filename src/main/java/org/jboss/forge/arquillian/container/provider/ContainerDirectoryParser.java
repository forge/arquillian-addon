/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian.container.provider;

import org.arquillian.container.chameleon.Loader;
import org.arquillian.container.chameleon.spi.model.Target;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.forge.arquillian.container.model.Container;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
public class ContainerDirectoryParser {

    @Inject
    ContainerIndexLocationProvider containerDirectoryLocationProvider;

    public List<Container> getContainers() throws IOException {
           return mapContainersFromConfigutaionFile();
    }

    private List<Container> mapContainersFromConfigutaionFile() {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            List<Container> parsedContainers = objectMapper.readValue(
                containerDirectoryLocationProvider.getUrl(),
                new TypeReference<List<Container>>() {
                });

            Loader loader = new Loader();
            final org.arquillian.container.chameleon.spi.model.Container[] containers = loader.loadContainers(Target.class.getClassLoader().getResourceAsStream("chameleon/default/containers.yaml"));

            parsedContainers.stream()
                .filter(container -> container.getGroupId() == null && container.getArtifactId() == null)
                .forEach(container -> {
                    try {
                        container.setGroupIdAndArtifactIdFromChameleonConfiguration(containers);
                    } catch (Exception e) {
                        throw new IllegalStateException("Couldn't set Group Id & Artifact Id for container" + container.getName());
                    }
                });

            return Collections.unmodifiableList(parsedContainers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
