package org.jboss.forge.arquillian.api;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;

@FacetConstraint(TestFrameworkFacet.class)
@FacetConstraint(MetadataFacet.class)
@FacetConstraint(DependencyFacet.class)
public abstract class AlgeronSetupFacet extends AbstractVersionedFacet {

   public abstract DependencyBuilder createContractLibraryDependency();
   public abstract DependencyBuilder createAlgeronDependency();
   public abstract String getVersionPropertyName();

   @Override
   protected Coordinate getVersionedCoordinate() {
      return createContractLibraryDependency().getCoordinate();
   }

   @Override
   public boolean install() {
      if(getVersion() != null) {
         installDependencies();
         return true;
      }
      return false;
   }

   private void installDependencies()
   {
      installContractLibrary(createContractLibraryDependency());
      installAlgeron(createAlgeronDependency());
   }

   private void installContractLibrary(DependencyBuilder contractsDependency) {
      if (hasEffectiveDependency(contractsDependency)) {
         return;
      }

      final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
      final MetadataFacet metadataFacet = getFaceted().getFacet(MetadataFacet.class);

      metadataFacet.setDirectProperty(getVersionPropertyName(), getVersion());
      dependencyFacet.addDirectDependency(contractsDependency.setVersion(wrap(getVersionPropertyName())));
   }

   private void installAlgeron(DependencyBuilder algeronDependency)
   {
      if (hasEffectiveDependency(algeronDependency))
      {
         return;
      }

      // version of Algeron is not required because it is provided by universe.
      final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
      dependencyFacet.addDirectDependency(algeronDependency);

   }

   @Override
   public boolean isInstalled() {
      return false;
   }

   private boolean hasEffectiveDependency(DependencyBuilder frameworkDependency)
   {
      final DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
      return dependencyFacet.hasEffectiveDependency(frameworkDependency);
   }

   private String wrap(String versionPropertyName)
   {
      return "${" + versionPropertyName + "}";
   }
}
