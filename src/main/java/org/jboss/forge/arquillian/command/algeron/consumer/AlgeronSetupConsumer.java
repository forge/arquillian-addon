package org.jboss.forge.arquillian.command.algeron.consumer;

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
import org.jboss.forge.arquillian.command.algeron.AbstractAlgeronCommand;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;
import org.jboss.forge.arquillian.testframework.algeron.AlgeronConsumer;

import javax.inject.Inject;
import java.util.Arrays;

public class AlgeronSetupConsumer extends AbstractAlgeronCommand {

    @Inject
    private FacetFactory facetFactory;

    @Inject
    @WithAttributes(shortName = 'l', label = "Contracts Library", type = InputType.DROPDOWN)
    private UISelectOne<ContractConsumerLibrary> contractsLibrary;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Setup Consumer")
            .description("This addon will help you setup Arquillian Algeron for Consumer side");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(contractsLibrary);

        contractsLibrary.setValueChoices(Arrays.asList(ContractConsumerLibrary.values()));
        contractsLibrary.setItemLabelConverter(element -> element.name().toLowerCase());
        contractsLibrary.setDefaultValue(ContractConsumerLibrary.PACT);
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {

        AlgeronConsumer algeronConsumerFacet = facetFactory.create(getSelectedProject(context), AlgeronConsumer.class);

        algeronConsumerFacet.setContractLibrary(contractsLibrary.getValue());
        final String contractDefaultVersion = algeronConsumerFacet.getDefaultVersion();
        algeronConsumerFacet.setVersion(contractDefaultVersion);

        facetFactory.install(getSelectedProject(context), algeronConsumerFacet);
        return Results.success("Installed Arquillian Algeron Consumer " + contractsLibrary.getValue().name().toLowerCase() + " and contract library version " + contractDefaultVersion);
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
