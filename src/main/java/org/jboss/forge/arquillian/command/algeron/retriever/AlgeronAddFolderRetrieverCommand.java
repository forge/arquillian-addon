package org.jboss.forge.arquillian.command.algeron.retriever;

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

public class AlgeronAddFolderRetrieverCommand extends AbstractAlgeronRetrieverCommand {

    @Inject
    @WithAttributes(shortName = 'c', label = "Contracts Folder")
    private UIInput<String> contractFolder;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Folder Retriever")
            .description("This command registers a Folder Retriever for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(contractFolder);
        contractFolder.setDefaultValue("target/pacts");
    }

    @Override
    protected Map<String, String> getParameters() {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "folder");
        parameters.put("contractsFolder", contractFolder.getValue());
        return parameters;
    }

    @Override
    protected String getName() {
        return "Folder";
    }
}
