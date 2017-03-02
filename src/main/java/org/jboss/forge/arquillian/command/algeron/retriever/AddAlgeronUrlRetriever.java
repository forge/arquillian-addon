package org.jboss.forge.arquillian.command.algeron.retriever;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.util.URLUIValidator;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAlgeronUrlRetriever extends AbstractAlgeronRetrieverCommand {
    @Inject
    @WithAttributes(shortName = 'u', label = "Url to GET", required = true)
    private UIInput<String> url;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Url Retriever")
            .description("This command registers a Url Retriever for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        url.addValidator(new URLUIValidator());
        builder.add(url);
    }

    @Override
    protected Map<String, String> getParameters() {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "url");
        parameters.put("url", url.getValue());
        return parameters;
    }

    @Override
    protected String getName() {
        return "Url";
    }

}
