package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.api.YamlGenerator;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.furnace.util.Strings;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

public class CubeSetupCommand extends AbstractProjectCommand implements UIWizard {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    private ResourceFactory resourceFactory;

    @Inject
    @WithAttributes(shortName = 't', label = "Type", type = InputType.DROPDOWN)
    private UISelectOne<CubeSetupFacet> type;

    @Inject
    @WithAttributes(shortName = 'f', label = "File", required = true)
    private UIInput<String> file;

    @Inject
    @WithAttributes(shortName = 'm', label = "Docker Machine", type = InputType.CHECKBOX)
    private UIInput<Boolean> dockerMachine;

    @Inject
    @WithAttributes(shortName = 'n', label = "Docker Machine Name")
    private UIInput<String> machineName;

    @Inject
    @WithAttributes(shortName = 'd', label = "Docker File Name")
    private UIInput<String> dockerFileName;


    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder
            .add(type)
            .add(file)
            .add(dockerMachine)
            .add(machineName)
            .add(dockerFileName);

        type.setEnabled(true);

        type.setItemLabelConverter(source -> {
            if (source == null) {
                return null;
            }
            if (builder.getUIContext().getProvider().isGUI()) {
                return source.getType();
            }
            return getStringForCLIDisplay(source.getType());
        });

        type.setRequired(() -> true);

        file.setEnabled(true);

        dockerMachine.setEnabled(this::isDockerOrDockerCompose);
        dockerMachine.setRequired(this::isDockerOrDockerCompose);

        machineName.setEnabled(this::isDockerMachine);
        machineName.setRequired(this::isDockerMachine);

        dockerFileName.setEnabled(this::isDocker);

    }

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Cube Setup")
            .description("This addon will help you setup Arquillian Cube.");
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        CubeSetupFacet selectedTypeFacet = type.getValue();
        final String msg = checkResourcesExists(context);
        if (msg != null) {
            return Results.fail(msg);
        }

        selectedTypeFacet.setConfigurationParameters(getParametersForExtensionCongiguration());
        try {
            facetFactory.install(getSelectedProject(context), selectedTypeFacet);

            return Results.success("Installed Cube" + selectedTypeFacet.getType());
        } catch (Exception e) {
            return Results.fail("Could not install Cube" + selectedTypeFacet.getType(), e);
        }
    }


    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

    private boolean isDockerMachine() {
        return dockerMachine.hasValue() && dockerMachine.getValue();
    }

    private boolean isDockerOrDockerCompose() {
        return type.hasValue() && Stream.of(type.getValue().getType())
            .anyMatch(x -> x.equals("Docker") || x.equals("Docker Compose"));
    }

    private boolean isDocker() {
        return type.hasValue() && Stream.of(type.getValue().getType())
            .anyMatch(x -> x.equals("Docker"));
    }

    private Map<String, String> getParametersForExtensionCongiguration() throws IOException {
        Map<String, String> parameters = new LinkedHashMap<>();

        if (file.hasValue()) {
            if (isDocker()) {
                final String yamlSnippet = System.lineSeparator() + YamlGenerator.getYaml(getConfigParametersForDocker()).replaceAll("(?m)^", "\t\t");

                parameters.put("definitionFormat", "CUBE");
                parameters.put(type.getValue().getKeyForFileLocation(), yamlSnippet);

            } else {
                parameters.put(type.getValue().getKeyForFileLocation(), file.getValue());
            }
        }

        if (machineName.hasValue()) {
            parameters.put("machineName", machineName.getValue());
        }

        return parameters;
    }

    private Map<String, Object> getConfigParametersForDocker() {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> build = new LinkedHashMap<>();

        String imageParams = "dockerfileLocation: " + file.getValue() + System.lineSeparator() +
            "noCache: true" + System.lineSeparator() +
            "remove: true";

        if (dockerFileName.hasValue()) {
            imageParams += System.lineSeparator() + "dockerfileName: " + dockerFileName.getValue();
        }

        build.put("buildImage", imageParams);
        params.put("containerName", build);
        return params;
    }

    private String checkResourcesExists(UIExecutionContext context) throws IOException {
        if (file.hasValue()) {
            String fileNameOrUrl = file.getValue();
            if (!Strings.isURL(fileNameOrUrl)) {

                //If dockerFileName provided.
                if (dockerFileName.hasValue()) {
                    String dockerfileName = dockerFileName.getValue();
                    if (!fileNameOrUrl.contains(dockerfileName)) {
                        fileNameOrUrl += dockerfileName;
                    }
                }

                final File file = new File(fileNameOrUrl);
//                final ResourcesFacet facet = getSelectedProject(context).getFacet(ResourcesFacet.class);
//                final FileResource<?> resource = facet.getResource(fileNameOrUrl);
//                if (!resource.exists()) {
//                    return "Could not find provided file: " + fileNameOrUrl;
//                }

                if (!resourceFactory.getFileOperations().fileExists(file)) {
                    return "Could not find provided file: " + fileNameOrUrl;
                }
            }
        }
        return null;
    }

}
