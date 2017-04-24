package org.jboss.forge.arquillian.command.core;

import org.jboss.forge.addon.facets.FacetFactory;
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
import org.jboss.forge.arquillian.api.core.ArquillianFacet;
import org.jboss.forge.arquillian.api.core.testframework.TestFrameworkFacet;
import org.jboss.forge.arquillian.api.core.testframework.TestFrameworkInstallEvent;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.util.Collections;

public class AddTestFrameworkCommand extends AbstractProjectCommand implements UIWizard {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    @Any
    private Event<TestFrameworkInstallEvent> installEvent;

    @Inject
    @WithAttributes(shortName = 't', label = "Test Framework", type = InputType.DROPDOWN)
    private UISelectOne<TestFrameworkFacet> testFramework;

    @Inject
    @WithAttributes(shortName = 'n', label = "Test Framework Version", type = InputType.DROPDOWN)
    private UISelectOne<String> testFrameworkVersion;

    @Inject
    @WithAttributes(shortName = 's', label = "Standalone", type = InputType.CHECKBOX)
    private UIInput<Boolean> standalone;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Arquillian"))
            .name("Arquillian: Add TestFramework")
            .description("This addon will help you setup a Test Framework for Arquillian");
    }

    @Override
    public void initializeUI(final UIBuilder builder) throws Exception {
        builder.add(testFramework)
            .add(testFrameworkVersion)
            .add(standalone);

        testFramework.setEnabled(true);
        testFramework.setItemLabelConverter(source -> {
            if (source == null) {
                return null;
            }
            if (builder.getUIContext().getProvider().isGUI()) {
                return source.getFrameworkName();
            }
            return source.getFrameworkName().toLowerCase();
        });
        testFramework.setRequired(() -> true); // check if already installed

        testFrameworkVersion.setRequired(() -> true);
        testFrameworkVersion.setEnabled(() -> testFramework.hasValue());
        testFrameworkVersion.setValueChoices(() -> {
            if (testFrameworkVersion.isEnabled()) {
                return testFramework.getValue().getAvailableVersions();
            }
            return Collections.emptyList();
        });
        testFrameworkVersion.setDefaultValue(() -> {
            if (testFrameworkVersion.isEnabled()) {
                return testFrameworkVersion.isEnabled() ? testFramework.getValue().getDefaultVersion() : null;
            }
            return null;
        });

        standalone.setEnabled(true);
        standalone.setDefaultValue(false);

    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        TestFrameworkFacet selectedTestFramework = testFramework.getValue();
        try {
            selectedTestFramework.setStandalone(standalone.getValue());
            selectedTestFramework.setVersion(testFrameworkVersion.getValue());
            facetFactory.install(getSelectedProject(context), selectedTestFramework);

            return Results.success("Installed " + selectedTestFramework.getFrameworkName());
        } catch (Exception e) {
            return Results.fail("Could not install Test Framework " + selectedTestFramework.getFrameworkName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public NavigationResult next(UINavigationContext context) throws Exception {
        if (isNotStandalone()) {
            return Results.navigateTo(ContainerSetupWizard.class);
        }

        return null;
    }

    private boolean isNotStandalone() {
        return !this.standalone.getValue();
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
