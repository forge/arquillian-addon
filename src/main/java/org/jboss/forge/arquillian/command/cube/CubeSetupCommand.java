package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.Resource;
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
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.api.core.testframework.TestFrameworkFacet;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;
import org.jboss.forge.arquillian.model.cube.CubeConfiguration;
import org.jboss.forge.arquillian.util.YamlGenerator;
import org.jboss.forge.furnace.util.Strings;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @WithAttributes(shortName = 'f', label = "File Path", type = InputType.DROPDOWN)
    private UISelectOne<String> filePath;

    @Inject
    @WithAttributes(shortName = 'm', label = "Docker Machine Name")
    private UIInput<String> dockerMachineName;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder
            .add(type)
            .add(filePath)
            .add(dockerMachineName);

        final List<String> types = Arrays.stream(CubeConfiguration.values())
            .map(CubeConfiguration::getType)
            .collect(Collectors.toList());

        type.setValueChoices(() -> types);
        type.setEnabled(true);
        type.setRequired(() -> true);
        type.setItemLabelConverter(source -> {
            if (builder.getUIContext().getProvider().isGUI()) {
                return source;
            }
            return getStringForCLIDisplay(source);
        });

        filePath.setEnabled(() -> type.hasValue());
        filePath.setValueChoices(() -> {
            if (filePath.isEnabled()) {
                return getPossibleFilePaths(getSelectedProject(builder));
            }
            return Collections.emptyList();
        });

        dockerMachineName.setEnabled(() -> cubeSetupFacet.isDockerOrDockerCompose(type));
    }

    private List<String> getPossibleFilePaths(Project project) {
        List<String> resources = new ArrayList<>();
        listResources(resources, project.getRoot());

        Stream<String> stream = Stream.empty();
        if (cubeSetupFacet.isDocker(type)) {
            stream = resources.stream().filter(name -> !(name.endsWith(".xml") || isYaml(name) || isJson(name)));
        } else if (cubeSetupFacet.isDockerCompose(type)) {
            stream = resources.stream().filter(name -> isYaml(name));
        } else if (cubeSetupFacet.isKubernetes(type) || cubeSetupFacet.isOpenshift(type)) {
            stream = resources.stream().filter(name -> isYaml(name) || isJson(name));
        }

        return stream.map(name -> name.substring(project.getRoot().getFullyQualifiedName().length() + 1))
            .collect(Collectors.toList());
    }

    private boolean isYaml(String fileName) {
        return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
    }

    private boolean isJson(String fileName) {
        return fileName.endsWith(".json");
    }

    private void listResources(List<String> resources, Resource<?> resource) {
        final List<Resource<?>> childResources = resource.listResources(source -> !JavaResource.class.isInstance(source));
        childResources.forEach(child -> addResource(resources, child));
    }

    private void addResource(List<String> files, Resource<?> resource) {
        final String fullyQualifiedName = resource.getFullyQualifiedName();
        final boolean fileExistsAndIsDirectory = resourceFactory.getFileOperations().fileExistsAndIsDirectory(new File(fullyQualifiedName));

        if (fileExistsAndIsDirectory) {
            listResources(files, resource);
        } else {
            files.add(resource.getFullyQualifiedName());
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
        String imageParams = "context: " + dirPath + System.lineSeparator();
        final Optional<String> alternativeFileName = getAlternativeFileName(context);
        if (alternativeFileName.isPresent()) {
            imageParams += "dockerfile: " + alternativeFileName.get();
        }

        build.put("build", imageParams);
        params.put("containerName", build);

        return params;
    }

    private String getDockerDirectoryPath(UIExecutionContext context) {
        String filePath = this.filePath.getValue();
        final int dirPathLength = filePath.length();
        if (filePath.endsWith(DOCKER_FILE)) {
            filePath = filePath.substring(0, dirPathLength - DOCKER_FILE.length() - 1);
        } else {
            final Optional<String> alternativeFileName = getAlternativeFileName(context);
            if (alternativeFileName.isPresent()) {
                filePath = filePath.substring(0, dirPathLength - alternativeFileName.get().length() - 1);
            }
        }

        return filePath;
    }

    private Optional<String> getAlternativeFileName(UIExecutionContext context) {
        final String fullyQualifiedName = getSelectedProject(context).getRoot().getFullyQualifiedName();
        final File file = new File(fullyQualifiedName + File.separator + filePath.getValue());

        if (file.isFile() && !file.getName().equals(DOCKER_FILE)) {
            return Optional.of(file.getName());
        }

        return Optional.empty();
    }

}
