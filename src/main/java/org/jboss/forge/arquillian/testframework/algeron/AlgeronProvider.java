package org.jboss.forge.arquillian.testframework.algeron;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.algeron.AlgeronSetupFacet;
import org.jboss.forge.arquillian.container.model.ContractProviderLibrary;

public class AlgeronProvider extends AlgeronSetupFacet {

    private ContractProviderLibrary contractLibrary;

    @Override
    public DependencyBuilder createContractLibraryDependency() {
        final DependencyBuilder contractProvider = contractLibrary.getContractProvider();
        // Creates a new copy of object since enum ContractLibrary creation happens only once and for Forge this instance is not immutable
        return DependencyBuilder.create(contractProvider);
    }

    @Override
    public DependencyBuilder createAlgeronDependency() {
        final DependencyBuilder algeronProvider = contractLibrary.getAlgeronProvider();
        // Creates a new copy of object since enum ContractLibrary creation happens only once and for Forge this instance is not immutable
        return DependencyBuilder.create(algeronProvider);
    }

    @Override
    public String getVersionPropertyName() {
        return contractLibrary.getVersionPropertyName();
    }

    @Override
    public String getContractType() {
        return contractLibrary.name();
    }

    public void setContractLibrary(ContractProviderLibrary contractLibrary) {
        this.contractLibrary = contractLibrary;
    }
}
