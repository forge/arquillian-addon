package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.facets.FacetFactory;
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
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.api.cube.CubeSetupFacet;

import javax.inject.Inject;

import static org.jboss.forge.arquillian.util.StringUtil.getStringForCLIDisplay;

public class AddCubeDependencyCommand extends AbstractCubeCommand implements UIWizard {

    @Inject
    private FacetFactory facetFactory;

    @Inject
    @WithAttributes(shortName = 't', label = "Type", type = InputType.DROPDOWN)
    private UISelectOne<CubeSetupFacet> type;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(type);
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
    }

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Cube Setup")
            .description("This addon will help you setup Arquillian Cube Dependencies.");
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        CubeSetupFacet selectedTypeFacet = type.getValue();
        try {
            facetFactory.install(getSelectedProject(context), selectedTypeFacet);

            return Results.success("Installed " + selectedTypeFacet.getType());
        } catch (Exception e) {
            return Results.fail("Could not install" + selectedTypeFacet.getType(), e);
        }
    }

}
