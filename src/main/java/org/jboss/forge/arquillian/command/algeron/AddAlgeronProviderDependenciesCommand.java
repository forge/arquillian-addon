package org.jboss.forge.arquillian.command.algeron;

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
import org.jboss.forge.arquillian.api.TestFrameworkFacet;
import org.jboss.forge.arquillian.container.model.ContractProviderLibrary;
import org.jboss.forge.arquillian.testframework.algeron.AlgeronProvider;

import javax.inject.Inject;
import java.util.Arrays;

public class AddAlgeronProviderDependenciesCommand extends AbstractAlgeronCommand {

    @Inject
    private FacetFactory facetFactory;

    @Inject
    @WithAttributes(shortName = 'l', label = "Contracts Library", type = InputType.DROPDOWN)
    private UISelectOne<ContractProviderLibrary> contractsLibrary;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Setup Provider")
            .description("This addon will help you setup Arquillian Algeron for Provider side");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(contractsLibrary);

        contractsLibrary.setValueChoices(Arrays.asList(ContractProviderLibrary.values()));
        contractsLibrary.setItemLabelConverter(element -> element.name().toLowerCase());
        contractsLibrary.setDefaultValue(ContractProviderLibrary.PACT);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        AlgeronProvider algeronProviderFacet = facetFactory.create(getSelectedProject(context), AlgeronProvider.class);

        algeronProviderFacet.setContractLibrary(contractsLibrary.getValue());
        final String contractDefaultVersion = algeronProviderFacet.getDefaultVersion();
        algeronProviderFacet.setVersion(contractDefaultVersion);
        facetFactory.install(getSelectedProject(context), algeronProviderFacet);

        return Results.success("Installed Arquillian Algeron Provider " + contractsLibrary.getValue().name().toLowerCase() + " and contract library version " + contractDefaultVersion);
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasFacet(TestFrameworkFacet.class);
        }
        return parent;
    }

}
