package test.integration.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ClassContainer;

/**
 * Adds packages defined using {@link AddPackages} annotation. 
 */
public class PackagesArchiveProcessor implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (canEnhanceWithPackages(applicationArchive, testClass)) {
            final AddPackages packages = testClass.getAnnotation(AddPackages.class);
            ((ClassContainer) applicationArchive).addPackages(packages.recursive(), packages.value());
        }

    }

    private boolean canEnhanceWithPackages(Archive<?> applicationArchive, TestClass testClass) {
        return applicationArchive instanceof ClassContainer && testClass.isAnnotationPresent(AddPackages.class);
    }
}
