package org.jboss.forge.arquillian.container.model;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.command.algeron.AlgeronConsumerTestSetup;
import org.jboss.forge.arquillian.command.algeron.PactAlgeronConsumerTestSetup;

public enum ContractConsumerLibrary {

    PACT("version.pact",
        DependencyBuilder.create()
            .setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-algeron-pact-consumer")
            .setScopeType("test")
            .setPackaging("pom"),
        DependencyBuilder.create()
            .setGroupId("au.com.dius")
            .setArtifactId("pact-jvm-consumer_2.11")
            .setScopeType("test")
    ) {
        @Override
        public AlgeronConsumerTestSetup getAlgeronConsumerTestSetup() {
            return new PactAlgeronConsumerTestSetup();
        }
    };

    private String versionPropertyName;
    private DependencyBuilder algeronConsumer;
    private DependencyBuilder contractConsumer;

    ContractConsumerLibrary(String versionPropertyName, DependencyBuilder algeronConsumer, DependencyBuilder contractConsumer) {
        this.versionPropertyName = versionPropertyName;
        this.algeronConsumer = algeronConsumer;
        this.contractConsumer = contractConsumer;
    }

    public DependencyBuilder getAlgeronConsumer() {
        return algeronConsumer;
    }

    public DependencyBuilder getContractConsumer() {
        return contractConsumer;
    }

    public String getVersionPropertyName() {
        return versionPropertyName;
    }

    public abstract AlgeronConsumerTestSetup getAlgeronConsumerTestSetup();

}
