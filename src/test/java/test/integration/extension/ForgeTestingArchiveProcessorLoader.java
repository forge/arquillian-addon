package test.integration.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ForgeTestingArchiveProcessorLoader implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(ApplicationArchiveProcessor.class, LibrariesLoaderArchiveProcessor.class);
        builder.service(ApplicationArchiveProcessor.class, PackagesArchiveProcessor.class);
    }
}
