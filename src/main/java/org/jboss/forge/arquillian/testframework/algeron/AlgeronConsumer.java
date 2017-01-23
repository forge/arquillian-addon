package org.jboss.forge.arquillian.testframework.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.AlgeronSetupFacet;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;

public class AlgeronConsumer extends AlgeronSetupFacet {

   private ContractConsumerLibrary contractLibrary;

   @Override
   public DependencyBuilder createContractLibraryDependency() {
      return contractLibrary.getContractConsumer();
   }

   @Override
   public DependencyBuilder createAlgeronDependency() {
      return contractLibrary.getAlgeronConsumer();
   }

   @Override
   public String getVersionPropertyName() {
      return contractLibrary.getVersionPropertyName();
   }

   public void setContractLibrary(ContractConsumerLibrary contractLibrary) {
      this.contractLibrary = contractLibrary;
   }
}
