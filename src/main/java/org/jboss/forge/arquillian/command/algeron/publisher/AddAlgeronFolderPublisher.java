package org.jboss.forge.arquillian.command.algeron.publisher;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAlgeronFolderPublisher extends AbstractAlgeronPublisherCommand {

    @Inject
    @WithAttributes(shortName = 'o', label = "Output Folder", required = true)
    private UIInput<String> outputFolder;

    @Inject
    @WithAttributes(shortName = 'c', label = "Contracts Folder")
    private UIInput<String> contractFolder;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Folder Publisher")
            .description("This command registers a Folder Publisher for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(outputFolder).add(contractFolder);
        contractFolder.setDefaultValue("target/pacts");
    }

    @Override
    protected Map<String, String> getParameters() {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "folder");
        parameters.put("outputFolder", outputFolder.getValue());
        parameters.put("contractsFolder", contractFolder.getValue());
        return parameters;
    }

    @Override
    protected String getName() {
        return "Folder";
    }
}
