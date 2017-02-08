package org.jboss.forge.arquillian.testframework.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.api.algeron.AlgeronSetupFacet;
import org.jboss.forge.arquillian.container.model.ContractConsumerLibrary;

public class AlgeronConsumer extends AlgeronSetupFacet {

    private ContractConsumerLibrary contractLibrary;

    @Override
    public DependencyBuilder createContractLibraryDependency() {
        final DependencyBuilder contractConsumer = contractLibrary.getContractConsumer();
        // Creates a new copy of object since enum ContractLibrary creation happens only once and for Forge this instance is not immutable
        return DependencyBuilder.create(contractConsumer);
    }

    @Override
    public DependencyBuilder createAlgeronDependency() {
        final DependencyBuilder algeronConsumer = contractLibrary.getAlgeronConsumer();
        // Creates a new copy of object since enum ContractLibrary creation happens only once and for Forge this instance is not immutable
        return DependencyBuilder.create(algeronConsumer);
    }

    @Override
    public boolean isInstalled() {
        return super.isConsumerDependenciesInstalled();
    }

    @Override
    public String getVersionPropertyName() {
        return contractLibrary.getVersionPropertyName();
    }

    @Override
    public String getContractType() {
        return contractLibrary.name();
    }

    public void setContractLibrary(ContractConsumerLibrary contractLibrary) {
        this.contractLibrary = contractLibrary;
    }
}
