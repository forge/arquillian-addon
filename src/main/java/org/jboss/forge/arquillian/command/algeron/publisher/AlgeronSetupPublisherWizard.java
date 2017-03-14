package org.jboss.forge.arquillian.command.algeron.publisher;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.api.algeron.AlgeronConsumerFacet;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;

@FacetConstraint(AlgeronConsumerFacet.class)
public class AlgeronSetupPublisherWizard extends AbstractProjectCommand implements UIWizard {

    static final String PUBLISH_CONTRACTS = "publish-contracts";

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    @WithAttributes(shortName = 'p', label = "Publisher", type = InputType.DROPDOWN, required = true)
    private UISelectOne<AlgeronPublisher> publisher;

    @Inject
    @WithAttributes(shortName = 'l', label = "Publish Contracts")
    private UIInput<String> publishContracts;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Setup Publisher")
            .description("This wizard registers a Publisher for Algeron");
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
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(publisher).add(publishContracts);

        publisher.setValueChoices(Arrays.asList(AlgeronPublisher.values()));
        publisher.setItemLabelConverter(element -> element.name().toLowerCase());

        publishContracts.setDefaultValue("${env.publishcontracts:false}");

    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        return Results.success("Installed Algeron Publisher.");
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        Map<Object, Object> ctx = context.getUIContext().getAttributeMap();
        ctx.put(PUBLISH_CONTRACTS, publishContracts.getValue());
        return Results.navigateTo(publisher.getValue().getImplementingCommand());
    }
}
