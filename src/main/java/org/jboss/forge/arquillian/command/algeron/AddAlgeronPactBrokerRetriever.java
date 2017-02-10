package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
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

public class AddAlgeronPactBrokerRetriever extends AbstractAlgeronRetrieverCommand {

    @Inject
    @WithAttributes(shortName = 'h', label = "Host", required = true)
    private UIInput<String> host;

    @Inject
    @WithAttributes(shortName = 'p', label = "Port", required = true)
    private UIInput<Integer> port;

    @Inject
    @WithAttributes(shortName = 'r', label = "Protocol")
    private UIInput<String> protocol;

    @Inject
    @WithAttributes(shortName = 't', label = "Tags")
    private UIInput<String> tags;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Pact Broker Retriever")
            .description("This command registers a Pact Broker Retriever for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(host)
            .add(port)
            .add(protocol)
            .add(tags);
    }

    @Override
    protected DependencyBuilder getRetrieverDependency() {
        return DependencyBuilder.create().setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-algeron-pact-broker-retriever").setPackaging("pom").setScopeType("test");
    }

    @Override
    protected Map<String, String> getParameters() {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "pactbroker");
        parameters.put("host", host.getValue());
        parameters.put("port", Integer.toString(port.getValue()));


        if (protocol.hasValue()) {
            parameters.put("protocol", protocol.getValue());
        }

        if (tags.hasValue()) {
            parameters.put("tags", tags.getValue());
        }

        return parameters;

    }

    @Override
    protected String getName() {
        return "Pact Broker";
    }

}
