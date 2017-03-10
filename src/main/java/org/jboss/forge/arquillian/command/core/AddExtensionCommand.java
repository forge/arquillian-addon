package org.jboss.forge.arquillian.command.core;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.api.core.ArquillianExtensionFacet;
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.extension.Extension;
import org.jboss.forge.arquillian.extension.ExtensionResolver;

import javax.inject.Inject;

public class AddExtensionCommand extends AbstractProjectCommand implements UICommand {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    private ArquillianExtensionFacet facet;

    @Inject
    private ExtensionResolver resolver;

    @Inject
    @WithAttributes(shortName = 'e', label = "Arquillian Extension", type = InputType.DROPDOWN)
    private UISelectOne<Extension> arquillianExtension;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Add Extension")
            .description("This addon will help you setup the base Arquillian");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(arquillianExtension);

        arquillianExtension.setValueChoices(() -> resolver.getAvailableExtensions(getSelectedProject(builder.getUIContext())));
        arquillianExtension.setItemLabelConverter(Extension::getName);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        facetFactory.install(getSelectedProject(context), facet);
        facet.install(arquillianExtension.getValue());

        return Results.success("Installed Arquillian Extension " + arquillianExtension.getValue());
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasFacet(ArquillianFacet.class);
        }
        return parent;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }
}
