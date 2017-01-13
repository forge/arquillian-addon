package test.integration.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * Adds libraries defined using {@link AddDependencies} annotation. If dependency is defined in your projects pom.xml
 * specifying a version is not necessary. Otherwise fully qualified maven coordinates are required.
 */
public class LibrariesLoaderArchiveProcessor implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (canEnhanceWithLibraries(applicationArchive, testClass)) {
            final AddDependencies addDependencies = testClass.getAnnotation(AddDependencies.class);
            final File[] libraries = Maven.resolver().loadPomFromFile("pom.xml").resolve(addDependencies.value()).withTransitivity().asFile();
            ((LibraryContainer) applicationArchive).addAsLibraries(libraries);
        }

    }

    private boolean canEnhanceWithLibraries(Archive<?> applicationArchive, TestClass testClass) {
        return testClass.isAnnotationPresent(AddDependencies.class) && (applicationArchive instanceof LibraryContainer);
    }
}
