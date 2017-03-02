package org.jboss.forge.arquillian.command.algeron.retriever;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.arquillian.api.algeron.AlgeronRetrieverFacet;
import org.jboss.forge.arquillian.testframework.algeron.AlgeronProvider;

import javax.inject.Inject;
import java.util.Map;

public abstract class AbstractAlgeronRetrieverCommand extends AbstractProjectCommand implements UICommand {
    public static final DependencyBuilder NO_DEPENDENCY = DependencyBuilder.create();

    @Inject
    protected ProjectFactory projectFactory;

    @Inject
    protected FacetFactory facetFactory;

    /**
     * Method that returns the parameters to add as YAML in retrieverConfiguration section
     *
     * @return
     */
    protected abstract Map<String, String> getParameters();

    /**
     * Name of the retriever to print it correctly in logs
     *
     * @return
     */
    protected abstract String getName();

    /**
     * Method that can be overridden by implementations if retriever requires a new dependency
     *
     * @return
     */
    protected DependencyBuilder getRetrieverDependency() {
        return NO_DEPENDENCY;
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
    public boolean isEnabled(UIContext context) {
        Boolean parent = super.isEnabled(context);
        if (parent) {
            return getSelectedProject(context).hasFacet(AlgeronProvider.class);
        }
        return parent;
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {

        AlgeronRetrieverFacet algeronRetrieverFacet = facetFactory.create(getSelectedProject(context), AlgeronRetrieverFacet.class);
        algeronRetrieverFacet.setConfigurationParameters(getParameters());
        final DependencyBuilder retrieverDependency = getRetrieverDependency();
        if (retrieverDependency != NO_DEPENDENCY) {
            algeronRetrieverFacet.setRetrieverDependency(retrieverDependency);
        }

        facetFactory.install(getSelectedProject(context), algeronRetrieverFacet);
        return Results.success("Installed Arquillian Algeron " + getName() + " Retriever.");

    }

}
