package org.jboss.forge.arquillian.command;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.arquillian.api.ArquillianFacet;
import org.jboss.forge.arquillian.api.TestFrameworkFacet;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FacetConstraint(ArquillianFacet.class)
public class CreateTestCommand extends AbstractProjectCommand implements UICommand {

   @Inject
   private TemplateFactory templateFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(shortName = 'n', label = "Test Name", required = false, enabled = false)
   private UIInput<String> named;
   @Inject
   @WithAttributes(shortName = 'p', label = "Target Package", required = false, enabled = false)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(shortName = 't', label = "Targets", required = false, enabled = false)
   private UISelectMany<JavaClassSource> targets;

   @Inject
   @WithAttributes(shortName = 'e', label = "Enable JPA", required = false, enabled = false)
   private UIInput<Boolean> enableJPA;

   @Inject
   @WithAttributes(shortName = 'a', label = "Archive Type", defaultValue = "JAR", enabled = false)
   private UISelectOne<ArchiveType> archiveType;

   @Inject
   @WithAttributes(label = "Deployment testable", defaultValue = "true", description = "Defines if this deployment should be wrapped up based on the protocol so the testcase can be executed incontainer.")
   private UIInput<Boolean> testable;

   @Inject
   private Inflector inflector;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
              .category(Categories.create("Arquillian"))
              .name("Arquillian: Create Test")
              .description("This addon will help you create a test skeleton based on a given class");
   }

   @Override
   public void initializeUI(final UIBuilder builder) throws Exception
   {
      builder
              .add(targets).add(enableJPA).add(archiveType)
              .add(named).add(targetPackage)
              .add(testable);

      Project project = getSelectedProject(builder);
      final List<JavaClassSource> sources = new ArrayList<>();
      project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor() {
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

      if (isStandalone(project)) {
         named.setEnabled(true);
         named.setRequired(true);

         targetPackage.setEnabled(true);
         targetPackage.setRequired(true);
      } else {
         targets.setRequired(true);
         targets.setEnabled(true);

         enableJPA.setEnabled(true);
         archiveType.setEnabled(true);
      }

      targets.setItemLabelConverter(source -> source == null ? null : source.getQualifiedName());

      targets.setValueChoices(sources);

      UISelection<Object> initialSelection = builder.getUIContext().getInitialSelection();
      if (initialSelection.get() instanceof JavaResource)
      {
         JavaResource javaResource = (JavaResource) initialSelection.get();
         JavaType<?> javaType = javaResource.getJavaType();
         if (javaType.isClass())
         {
            targets.setDefaultValue(Collections.singletonList((JavaClassSource) javaType));
         }
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      List<Result> results = new ArrayList<>();
      UIContext uiContext = context.getUIContext();
      List<JavaResource> resources = new ArrayList<>();

      final Project project = getSelectedProject(context);

      if (targets.hasValue())
      {
         for (JavaClassSource clazz : targets.getValue())
         {
            JavaResource test = createTest(project, clazz, enableJPA.getValue(),
                    archiveType.getValue());
            resources.add(test);
            results.add(Results.success("Created test class " + test.getJavaType().getQualifiedName()));
         }
      }
      else
      {
         JavaResource test = createStandaloneTest(project, targetPackage.getValue(), named.getValue());
         resources.add(test);
         results.add(Results.success("Created test class " + test.getJavaType().getQualifiedName()));
      }

      if (!resources.isEmpty())
         uiContext.setSelection(resources);
      return Results.aggregate(results);
   }

   @Override
   protected boolean isProjectRequired() {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory() {
      return projectFactory;
   }

   private JavaResource createStandaloneTest(Project project, String targetPackage, String testName) throws Exception
   {
      final TestFrameworkFacet testFrameworkFacet = project.getFacet(TestFrameworkFacet.class);
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      final Template template = getTemplateFor(testFrameworkFacet.getTemplateStandaloneLocation());

      final Map<String, Object> context = initializeFreeMarkerContextForStandalone(targetPackage, testName);
      final JavaSource<?> testClass = Roaster.parse(JavaSource.class, template.process(context));
      return java.saveTestJavaSource(testClass);
   }

   private JavaResource createTest(Project project, JavaClassSource classUnderTest, boolean enableJPA, ArchiveType type)
           throws IOException
   {
      final TestFrameworkFacet testFrameworkFacet = project.getFacet(TestFrameworkFacet.class);
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      final Template template = getTemplateFor(testFrameworkFacet.getTemplateLocation());

      final Map<String, Object> context = initializeFreeMarkerContext(enableJPA, type, classUnderTest);
      final JavaSource<?> testClass = Roaster.parse(JavaSource.class, template.process(context));
      return java.saveTestJavaSource(testClass);
   }

   private Template getTemplateFor(String name)
   {
      final Resource<URL> resource = resourceFactory.create(getClass().getResource(name));
      return templateFactory.create(resource, FreemarkerTemplate.class);
   }

   private boolean isStandalone(Project project)
   {
      final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

      return dependencyFacet.getDependencies().stream()
              .map(dependency -> dependency.getCoordinate())
              .anyMatch(coordinate ->
                      "org.arquillian.universe".equals(coordinate.getGroupId()) &&
                              coordinate.getArtifactId().contains("standalone"));

   }

   private Map<String, Object> initializeFreeMarkerContext(boolean enableJPA, ArchiveType type, JavaSource<?> javaSource)
   {
      final Map<String, Object> context = new HashMap<>();
      context.put("package", javaSource.getPackage());
      context.put("ClassToTest", javaSource.getName());
      context.put("classToTest", inflector.lowerCamelCase(javaSource.getName()));
      context.put("packageImport", javaSource.getPackage());
      context.put("enableJPA", enableJPA);
      context.put("archiveType", type);
      context.put("testable", testable.getValue());
      return context;
   }

   private Map<String, Object> initializeFreeMarkerContextForStandalone(String targetPackage, String testName)
   {
      final Map<String, Object> context = new HashMap<>();
      context.put("package", targetPackage);
      context.put("ClassToTest", testName);
      return context;
   }
}
