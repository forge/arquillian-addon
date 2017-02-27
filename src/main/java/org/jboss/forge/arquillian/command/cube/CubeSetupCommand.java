package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileOperations;
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
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.api.TestFrameworkFacet;
import org.jboss.forge.arquillian.api.YamlGenerator;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.arquillian.container.model.CubeConfiguration;
import org.jboss.forge.furnace.util.Strings;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

public class CubeSetupCommand extends AbstractProjectCommand implements UIWizard {

    private CubeSetupFacet cubeSetupFacet;

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    private ResourceFactory resourceFactory;

    @Inject
    @WithAttributes(shortName = 't', label = "Type", type = InputType.DROPDOWN)
    private UISelectOne<String> type;

    @Inject
    @WithAttributes(shortName = 'f', label = "File", required = true)
    private UIInput<String> filePath;

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
            .add(filePath)
            .add(dockerMachine)
            .add(machineName)
            .add(dockerFileName);

        final List<String> types = Arrays.stream(CubeConfiguration.values())
            .map(CubeConfiguration::getType)
            .collect(Collectors.toList());

        type.setValueChoices(types);
        type.setEnabled(true);
        type.setRequired(() -> true);
        type.setItemLabelConverter(source -> {
            if (source == null) {
                return null;
            }
            if (builder.getUIContext().getProvider().isGUI()) {
                return source;
            }
            return getStringForCLIDisplay(source);
        });

        filePath.setEnabled(true);

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

        final String msg = checkResourcesExists(context);

        if (type.hasValue()) {
            setCubeSetupFacet();
            setCubeConfigurationParamteres(context);
        }

        if (msg != null) {
            return Results.fail(msg);
        }

        final CubeConfiguration cubeConfiguration = cubeSetupFacet.getCubeConfiguration();

        try {
            facetFactory.install(getSelectedProject(context), cubeSetupFacet);

            return Results.success("Installed Cube " + cubeConfiguration.getType() + " & updated arquillian configuration.");
        } catch (Exception e) {
            return Results.fail("Could not install Cube " + cubeConfiguration.getType(), e);
        }
    }

    private void setCubeSetupFacet() {

        CubeSetupFacet cubeSetupFacet = new CubeSetupFacet();
        if (isKubernetes()) {
            cubeSetupFacet.setCubeConfiguration(CubeConfiguration.KUBERNETES);
        } else if (isDocker()) {
            cubeSetupFacet.setCubeConfiguration(CubeConfiguration.DOCKER);
        } else if (isDockerCompose()) {
            cubeSetupFacet.setCubeConfiguration(CubeConfiguration.DOCKER_COMPOSE);
        } else if (isOpenshift()) {
            cubeSetupFacet.setCubeConfiguration(CubeConfiguration.OPENSHIFT);
        }

        this.cubeSetupFacet = cubeSetupFacet;
    }

    private void setCubeConfigurationParamteres(UIExecutionContext context) throws IOException {
        cubeSetupFacet.setConfigurationParameters(getParametersForExtensionConfiguration(context));
    }

    private boolean isDockerMachine() {
        return dockerMachine.hasValue() && dockerMachine.getValue();
    }

    private boolean isDockerOrDockerCompose() {
        return isDocker() || isDockerCompose();
    }

    private boolean isDocker() {
        return type.hasValue() && Stream.of(getStringForCLIDisplay(type.getValue()))
            .anyMatch(x -> x.equals("docker"));
    }

    private boolean isKubernetes() {
        return type.hasValue() && Stream.of(getStringForCLIDisplay(type.getValue()))
            .anyMatch(x -> x.equals("kubernetes"));
    }

    private boolean isDockerCompose() {
        return type.hasValue() && Stream.of(getStringForCLIDisplay(type.getValue()))
            .anyMatch(x -> x.equals("docker-compose"));
    }

    private boolean isOpenshift() {
        return type.hasValue() && Stream.of(getStringForCLIDisplay(type.getValue()))
            .anyMatch(x -> x.equals("openshift"));
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasAllFacets(ArquillianFacet.class, TestFrameworkFacet.class);
        }
        return parent;
    }

    private Map<String, String> getParametersForExtensionConfiguration(UIExecutionContext context) throws IOException {
        Map<String, String> parameters = new LinkedHashMap<>();

        if (filePath.hasValue()) {
            if (isDocker()) {
                addDockerParameters(parameters);
            } else {
                if (isKubernetes()) {
                    addKubernetesParameters(parameters, context);
                } else {
                    parameters.put(this.cubeSetupFacet.getCubeConfiguration().getKeyForFileLocation(), filePath.getValue());
                }
            }
        }

        if (machineName.hasValue()) {
            parameters.put("machineName", machineName.getValue());
        }

        return parameters;
    }

    private void addKubernetesParameters(Map<String, String> parameters, UIExecutionContext context) {
        final String filePathValue = filePath.getValue();

        if (Strings.isURL(filePathValue)) {
            parameters.put(cubeSetupFacet.getCubeConfiguration().getKeyForFileLocation(), filePathValue);
        } else {
            final String fileName = classPathResource(context);
            if (fileName != null) {
                parameters.put("env.config.resource.name", fileName);
            }
        }
    }

    private String classPathResource(UIExecutionContext context) {
        final String filePathValue = filePath.getValue();
        final String[] path = filePathValue.split(File.separator);
        final String fileName = path[path.length - 1];
        final ResourcesFacet facet = getSelectedProject(context).getFacet(ResourcesFacet.class);

        if (facet.getTestResource(fileName).exists()) {
            final String fileNameWOExtesion = fileName.substring(0, fileName.indexOf("."));

            if (!fileNameWOExtesion.equals("kubernetes")) {
                return fileName;
            }
        }

        return null;
    }

    private void addDockerParameters(Map<String, String> parameters) {
        final String yamlSnippet = System.lineSeparator() + YamlGenerator.getYaml(getConfigParametersForDocker()).replaceAll("(?m)^", "    ");

        parameters.put("definitionFormat", "CUBE");
        parameters.put(cubeSetupFacet.getCubeConfiguration().getKeyForFileLocation(), yamlSnippet);
    }

    private Map<String, Object> getConfigParametersForDocker() {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> build = new LinkedHashMap<>();

        String imageParams = "dockerfileLocation: " + filePath.getValue() + System.lineSeparator() +
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
        if (filePath.hasValue()) {
            String filePath = this.filePath.getValue();
            if (!Strings.isURL(filePath)) {

                if (dockerFileName.hasValue()) {
                    String dockerfileName = dockerFileName.getValue();
                    if (!filePath.contains(dockerfileName)) {
                        if (!filePath.endsWith(File.separator)) {
                            filePath += File.separator;
                        }
                        filePath += dockerfileName;
                    }
                }

                if (!fileExistsAndisNotDir(context, filePath)) {
                    return "Could not find provided filePath: " + filePath + " or it is directory which is not allowed.";
                }
            }
        }
        return null;
    }

    private boolean fileExistsAndisNotDir(UIExecutionContext context, String fileName) {
        final String relativePath = getSelectedProject(context).getRoot().getFullyQualifiedName() + File.separator + fileName;
        final File file = new File(relativePath);

        final FileOperations fileOperations = resourceFactory.getFileOperations();
        return fileOperations.fileExists(file) && !file.isDirectory();
    }
}
