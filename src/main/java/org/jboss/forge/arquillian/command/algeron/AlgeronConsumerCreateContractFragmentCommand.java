package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.api.algeron.AlgeronSetupFacet;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;
import org.jboss.forge.arquillian.testframework.algeron.AlgeronConsumer;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgeronConsumerCreateContractFragmentCommand extends AbstractProjectCommand implements UICommand  {

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(shortName = 'j', label = "Test Class", required = true)
   private UISelectOne<JavaClassSource> testClass;

   @Inject
   @WithAttributes(shortName = 'c', label = "Consumer Name", required = true)
   private UIInput<String> consumer;

   @Inject
   @WithAttributes(shortName = 'p', label = "Provider Name", required = true)
   private UIInput<String> provider;

   @Inject
   @WithAttributes(shortName = 'f', label = "Fragment Name", required = true)
   private UIInput<String> fragment;

   @Inject
   @WithAttributes(shortName = 't', label = "Contract Type", required = false, enabled = false)
   private UISelectOne<ContractConsumerLibrary> contractType;

   @Override
   protected boolean isProjectRequired() {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory() {
      return projectFactory;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context) {
      return Metadata.from(super.getMetadata(context), getClass())
              .category(Categories.create("Algeron"))
              .name("Arquillian Algeron: Create Contract Fragment")
              .description("This command creates skeleton for contract fragment on given test");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception {
      builder.add(contractType)
              .add(testClass)
              .add(consumer)
              .add(provider)
              .add(fragment);

      final Project project = getSelectedProject(builder);

      final List<JavaClassSource> sources = new ArrayList<>();
      project.getFacet(JavaSourceFacet.class).visitJavaTestSources(new JavaResourceVisitor() {
         @Override
         public void visit(VisitContext context, JavaResource javaResource) {
            JavaType<?> javaType;
            try
            {
               javaType = javaResource.getJavaType();
               if (javaType.isClass()) {
                  sources.add((JavaClassSource) javaType);
               }
            }
            catch (FileNotFoundException e)
            {
               // Do nothing
            }
         }
      });

      this.testClass.setValueChoices(sources);

      final Configuration configuration = project.getFacet(ConfigurationFacet.class).getConfiguration();
      final String contractType = configuration.getString(AlgeronSetupFacet.CONTRACT_TYPE);

      if (contractType != null && ! contractType.isEmpty())
      {
         if (! this.contractType.hasValue())
         {
            this.contractType.setValue(ContractConsumerLibrary.valueOf(contractType));
         }
      }
      else
      {
         this.contractType.setValueChoices(Arrays.asList(ContractConsumerLibrary.values()));
         this.contractType.setItemLabelConverter(element -> element.name().toLowerCase());
         this.contractType.setRequired(true);
         this.contractType.setEnabled(true);
      }

      this.testClass.setItemLabelConverter(JavaClassSource::getQualifiedName);

   }

   @Override
   public boolean isEnabled(UIContext context) {
      Boolean parent = super.isEnabled(context);
      if(parent) {
         return getSelectedProject(context).hasFacet(AlgeronConsumer.class);
      }
      return parent;
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception {

      final Project project = getSelectedProject(context);

      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      final AlgeronConsumerTestSetup algeronConsumerTestSetup = this.contractType.getValue().getAlgeronConsumerTestSetup();

      JavaClassSource updatedTest = algeronConsumerTestSetup.updateTest(this.testClass.getValue(),
              this.consumer.getValue(), this.provider.getValue(),
              this.fragment.getValue());

      java.saveTestJavaSource(updatedTest);

      return Results.success(String.format("Contract Fragment %s added at %s test.",
              this.fragment.getValue(), this.testClass.getValue().getName()));
   }
}
