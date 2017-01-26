package org.jboss.forge.arquillian.command;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.arquillian.api.AlgeronPublisherFacet;
import org.jboss.forge.arquillian.api.AlgeronSetupFacet;

import javax.inject.Inject;
import java.util.Map;

public abstract class AbstractAlgeronPublisherCommand extends AbstractProjectCommand implements UICommand
{

   public static final DependencyBuilder NO_DEPENDENCY = null;
   @Inject
   protected ProjectFactory projectFactory;

   @Inject
   protected FacetFactory facetFactory;

   /**
    * Method that returns the parameters to add as YAML in publishConfiguration section
    * @return
    */
   protected abstract Map<String, String> getParameters();

   /**
    * Name of the publisher to print it correctly in logs
    * @return
    */
   protected abstract String getName();

   /**
    * Method that can be overridden by implementations if publisher requires a new dependency
    * @return
    */
   protected DependencyBuilder getPublisherDependency()
   {
      return NO_DEPENDENCY;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      Boolean parent = super.isEnabled(context);
      if(parent) {
         return getSelectedProject(context).hasFacet(AlgeronSetupFacet.class);
      }
      return parent;
   }


   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {

      Map<Object, Object> ctx = context.getUIContext().getAttributeMap();
      final String publishContracts = (String) ctx.get(AlgeronPublisherWizard.PUBLISH_CONTRACTS);

      AlgeronPublisherFacet algeronPublisherFacet = facetFactory.create(getSelectedProject(context), AlgeronPublisherFacet.class);
      algeronPublisherFacet.setConfigurationParameters(getParameters());
      algeronPublisherFacet.setPublishContracts(publishContracts);
      algeronPublisherFacet.setPublisherDependency(getPublisherDependency());

      facetFactory.install(getSelectedProject(context), algeronPublisherFacet);
      return Results.success("Installed Arquillian Algeron "+ getName() +" Publisher.");

   }

}
