package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.inject.Inject;

public class AlgeronProviderValidateContractCommand extends AbstractProjectCommand implements UICommand {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    @WithAttributes(shortName = 'j', label = "Test Class", required = true)
    private UISelectOne<JavaClassSource> testClass;

    @Inject
    @WithAttributes(shortName = 'p', label = "Provider Name", required = true)
    private UIInput<String> provider;

    @Inject
    @WithAttributes(shortName = 't', label = "Contract Type", required = false, enabled = false)
    private UISelectOne<ContractConsumerLibrary> contractType;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Create Provider Test")
            .description("This command creates skeleton for validating provider on given test");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {

    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        return null;
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }
}
