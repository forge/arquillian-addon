package org.jboss.forge.arquillian.container;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.arquillian.container.chameleon.Loader;
import org.arquillian.container.chameleon.spi.model.Dist;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.forge.arquillian.container.model.Container;

public class ChameleonContainerLoader {

    private static final Pattern pattern = Pattern.compile("(?<=arquillian container).*(?=remote|managed|embedded)");

    public void setGroupIdAndArtifactIdFromChameleon(List<Container> containerList) throws Exception {

        final org.arquillian.container.chameleon.spi.model.Container[] chameleonContainers = loadContainers();

        containerList.forEach(container -> {
            Matcher m = pattern.matcher(container.getName().toLowerCase());
            if (m.find()) {
                Optional<String> containerName = Optional.ofNullable(m.group(0));
                if (containerName.isPresent()) {
                    String finalContainerName = containerName.get().trim();
                    Arrays.stream(chameleonContainers)
                        .filter(containerPredicate -> {
                            final boolean matched = Arrays.stream(containerPredicate.getAdapters())
                                .anyMatch(adapter -> adapter.getType()
                                        .equalsIgnoreCase(container.getContainerType().toString()));

                            return matched && containerPredicate.getName().equalsIgnoreCase(finalContainerName);
                        })
                        .forEach(chameleonContainer -> setGroupIdAndArtifactID(chameleonContainer, container));
                }
            }
        });
    }

    private org.arquillian.container.chameleon.spi.model.Container[] loadContainers() throws Exception {
        Loader loader = new Loader();

        return loader.loadContainers(
            Target.class.getClassLoader().getResourceAsStream("chameleon/default/containers.yaml"));
    }

    private void setGroupIdAndArtifactID(org.arquillian.container.chameleon.spi.model.Container chameleonContainer,
        Container container) {
        final Dist dist = chameleonContainer.getDist();
        final String[] split = dist.coordinates().split(":");
        if (split.length >= 2) {
            container.setGroupId(split[0]);
            container.setArtifactId(split[1]);
        } else {
            throw new IllegalStateException(
                "Group Id or Artifact Id is missing in distribution of chameleon configuration for chameleonContainer: "
                    + chameleonContainer.getName());
        }
    }
}
