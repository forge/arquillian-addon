package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.container.model.AlgeronRetriever;

import javax.inject.Inject;
import java.util.Arrays;

//@FacetConstraint(AlgeronConsumer.class)
public class AlgeronRetrieverWizard extends AbstractProjectCommand implements UIWizard
{

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(shortName = 'p', label = "Retriever", type = InputType.DROPDOWN, required = true)
   private UISelectOne<AlgeronRetriever> retriever;


   @Override
   public UICommandMetadata getMetadata(UIContext context) {
      return Metadata.from(super.getMetadata(context), getClass())
              .category(Categories.create("Algeron"))
              .name("Arquillian Algeron: Setup Retriever")
              .description("This wizard registers a Retriever for Algeron");
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
      builder.add(retriever);

      retriever.setValueChoices(Arrays.asList(AlgeronRetriever.values()));
      retriever.setItemLabelConverter(element -> element.name().toLowerCase());
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception {
      return Results.success("Installed Algeron Retriever.");
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception {
      return Results.navigateTo(retriever.getValue().getImplementingCommand());
   }

}
