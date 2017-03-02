package org.jboss.forge.arquillian.container.model;


import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.command.algeron.provider.AlgeronProviderTestSetup;
import org.jboss.forge.arquillian.command.algeron.provider.PactAlgeronProviderTestSetup;

public enum ContractProviderLibrary {

    PACT("version.pact",
        DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-algeron-pact-provider")
            .setScopeType("test")
            .setPackaging("pom"),
        DependencyBuilder.create()
            .setGroupId("au.com.dius")
            .setArtifactId("pact-jvm-provider_2.11")
            .setScopeType("test")
    )
        {
            @Override
            public AlgeronProviderTestSetup getAlgeronProviderTestSetup() {
                return new PactAlgeronProviderTestSetup();
            }
        };

    private String versionPropertyName;
    private DependencyBuilder algeronProvider;
    private DependencyBuilder contractProvider;

    ContractProviderLibrary(String versionPropertyName, DependencyBuilder algeronProvider, DependencyBuilder contractProvider) {
        this.versionPropertyName = versionPropertyName;
        this.algeronProvider = algeronProvider;
        this.contractProvider = contractProvider;
    }

    public abstract AlgeronProviderTestSetup getAlgeronProviderTestSetup();

    public String getVersionPropertyName() {
        return versionPropertyName;
    }

    public DependencyBuilder getAlgeronProvider() {
        return algeronProvider;
    }

    public DependencyBuilder getContractProvider() {
        return contractProvider;
    }

}
