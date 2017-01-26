package org.jboss.forge.arquillian.command;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAlgeronUrlPublisher extends AbstractAlgeronPublisherCommand
{

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(shortName = 'u', label = "Url to POST", required = true)
   private UIInput<String> url;

   @Inject
   @WithAttributes(shortName = 'c', label = "Contracts Folder")
   private UIInput<String> contractFolder;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
              .category(Categories.create("Algeron"))
              .name("Arquillian Algeron: Add Url Publisher")
              .description("This command registers a Url Publisher for Algeron");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(url).add(contractFolder);
      contractFolder.setDefaultValue("target/pacts");
   }

   @Override
   protected Map<String, String> getParameters()
   {
      Map<String, String> parameters = new LinkedHashMap<>();
      parameters.put("provider", "url");
      parameters.put("url", url.getValue());
      parameters.put("contractsFolder", contractFolder.getValue());
      return parameters;
   }

   @Override
   protected String getName() {
      return "Url";
   }
}
