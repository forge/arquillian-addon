package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileOperations;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.command.UICommand;
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
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.api.TestFrameworkFacet;
import org.jboss.forge.arquillian.api.YamlGenerator;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.arquillian.model.cube.CubeConfiguration;
import org.jboss.forge.furnace.util.Strings;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

public class CubeSetupCommand extends AbstractProjectCommand implements UICommand {

    private static final String DOCKER_FILE = "Dockerfile";

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private CubeSetupFacet cubeSetupFacet;

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
    @WithAttributes(shortName = 'm', label = "Docker Machine Name")
    private UIInput<String> dockerMachineName;

    @Inject
    @WithAttributes(shortName = 'd', label = "Docker File Name")
    private UIInput<String> dockerFileName;


    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder
            .add(type)
            .add(filePath)
            .add(dockerMachineName)
            .add(dockerFileName);

        final List<String> types = Arrays.stream(CubeConfiguration.values())
            .map(CubeConfiguration::getType)
            .collect(Collectors.toList());

        type.setValueChoices(types);
        type.setEnabled(true);
        type.setRequired(() -> true);
        type.setItemLabelConverter(source -> {
            if (builder.getUIContext().getProvider().isGUI()) {
                return source;
            }
            return getStringForCLIDisplay(source);
        });

        filePath.setEnabled(true);

        if (type.hasValue()) {
            dockerFileName.setEnabled(cubeSetupFacet.isDocker(type));
            dockerMachineName.setEnabled(cubeSetupFacet.isDockerOrDockerCompose(type));
        }
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

        if (msg != null) {
            return Results.fail(msg);
        }

        if (type.hasValue()) {
            cubeSetupFacet.setCubeConfiguration(type);
            setCubeConfigurationParameters(context);
        }

        final CubeConfiguration cubeConfiguration = cubeSetupFacet.getCubeConfiguration();

        try {
            facetFactory.install(getSelectedProject(context), cubeSetupFacet);

            return Results.success("Installed Cube " + cubeConfiguration.getType() + " & updated arquillian configuration.");
        } catch (Exception e) {
            return Results.fail("Could not install Cube " + cubeConfiguration.getType(), e);
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

    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasAllFacets(ArquillianFacet.class, TestFrameworkFacet.class);
        }
        return parent;
    }

    private void setCubeConfigurationParameters(UIExecutionContext context) throws IOException {
        cubeSetupFacet.setConfigurationParameters(getParametersForExtensionConfiguration(context));
    }

    private Map<String, String> getParametersForExtensionConfiguration(UIExecutionContext context) throws IOException {
        Map<String, String> parameters = new LinkedHashMap<>();

        if (filePath.hasValue()) {
            if (cubeSetupFacet.isDocker(type)) {
                addDockerParameters(parameters, context);
            } else {
                if (cubeSetupFacet.isKubernetes(type)) {
                    addKubernetesParameters(parameters, context);
                } else {
                    parameters.put(this.cubeSetupFacet.getCubeConfiguration().getKeyForFileLocation(), filePath.getValue());
                }
            }
        }

        if (dockerMachineName.hasValue()) {
            parameters.put("machineName", dockerMachineName.getValue());
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

    private void addDockerParameters(Map<String, String> parameters, UIExecutionContext context) {
        final String yamlSnippet = System.lineSeparator() + getYamlSnippetForDockerComposeV2(context);

        parameters.put(cubeSetupFacet.getCubeConfiguration().getKeyForFileLocation(), yamlSnippet);
    }

    private String getYamlSnippetForDockerComposeV2(UIExecutionContext context) {

        Map<String, Object> compose = new LinkedHashMap<>();
        compose.put("version", "2");
        compose.put("services", YamlGenerator.toYml(getConfigParametersForDocker(context)));

        return YamlGenerator.toYml(compose).replaceAll("(?m)^", "    ");
    }

    private Map<String, Object> getConfigParametersForDocker(UIExecutionContext context) {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> build = new LinkedHashMap<>();

        final String dirPath = getDockerDirectoryPath(context);

        String imageParams = "context: " + dirPath +System.lineSeparator();

        final String dockerFileNameValue = dockerFileName.getValue();
        if (dockerFileName.hasValue() && !dockerFileNameValue.equals(DOCKER_FILE)) {
            imageParams +=  "dockerfile: " + dockerFileNameValue;
        } else {
            final String alternativeFileName = getAlternativeFileName(context);
            if (alternativeFileName != null) {
                imageParams +=  "dockerfile: " + alternativeFileName;
            }
        }

        build.put("build", imageParams);
        params.put("containerName", build);
        return params;
    }

    private String getDockerDirectoryPath(UIExecutionContext context) {
        String dirPath = filePath.getValue();
        String fileName = dockerFileName.getValue();
        final int dirPathLength = dirPath.length();
        if (dirPath.endsWith(DOCKER_FILE)) {
            dirPath = dirPath.substring(0, dirPathLength - DOCKER_FILE.length());
        } else if (dockerFileName.hasValue() && dirPath.endsWith(fileName)) {
            dirPath = dirPath.substring(0, dirPathLength - fileName.length());
        } else if (dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPathLength - 1);
        }
        final String fullyQualifiedName = getSelectedProject(context).getRoot().getFullyQualifiedName();
        final File file = new File(fullyQualifiedName + File.separator +  dirPath);

        if (file.isFile()) {
                dirPath = dirPath.substring(0, dirPathLength - file.getName().length() -1);
        }

        return dirPath;
    }

    private String getAlternativeFileName(UIExecutionContext context) {
        final String fullyQualifiedName = getSelectedProject(context).getRoot().getFullyQualifiedName();
        final File file = new File(fullyQualifiedName + File.separator +  filePath.getValue());

        if (file.isFile() && !file.getName().equals(DOCKER_FILE)) {
            return file.getName();
        } else {
            return null;
        }
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
