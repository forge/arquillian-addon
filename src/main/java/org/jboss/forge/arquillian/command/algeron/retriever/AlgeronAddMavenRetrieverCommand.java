package org.jboss.forge.arquillian.command.algeron.retriever;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.util.MavenCoordinatesUIValidator;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlgeronAddMavenRetrieverCommand extends AbstractAlgeronRetrieverCommand {

    @Inject
    @WithAttributes(shortName = 'c', label = "Maven Coordinates", required = true)
    private UIInput<String> mavenCoordinates;

    @Inject
    @WithAttributes(shortName = 'o', label = "Offline", type = InputType.CHECKBOX)
    private UIInput<Boolean> offline;

    @Inject
    @WithAttributes(shortName = 's', label = "Custom Settings")
    private UIInput<String> customSettings;

    @Inject
    @WithAttributes(shortName = 'r', label = "Remote Repository")
    private UIInput<String> remoteRepository;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Maven Retriever")
            .description("This command registers a Maven Retriever for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        mavenCoordinates.addValidator(new MavenCoordinatesUIValidator());
        builder.add(mavenCoordinates)
            .add(offline)
            .add(customSettings)
            .add(remoteRepository);
    }

    @Override
    protected DependencyBuilder getRetrieverDependency() {
        return DependencyBuilder.create().setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-algeron-maven-retriever").setPackaging("pom").setScopeType("test");
    }

    @Override
    protected Map<String, String> getParameters() {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "maven");
        parameters.put("coordinates", mavenCoordinates.getValue());

        if (offline.hasValue()) {
            parameters.put("offline", Boolean.toString(offline.getValue()));
        }

        if (customSettings.hasValue()) {
            parameters.put("customSettings", customSettings.getValue());
        }

        if (remoteRepository.hasValue()) {
            parameters.put("remoteRepository", remoteRepository.getValue());
        }

        return parameters;

    }

    @Override
    protected String getName() {
        return "Maven";
    }

}
