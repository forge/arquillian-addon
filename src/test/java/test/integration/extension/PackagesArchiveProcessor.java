package test.integration.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ClassContainer;

/**
 * Adds packages defined using {@link AddPackage} annotation.
 */
public class PackagesArchiveProcessor implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (canEnhanceWithPackages(applicationArchive, testClass)) {
            final AddPackage[] packages = testClass.getJavaClass().getAnnotationsByType(AddPackage.class);
            for (AddPackage pkg : packages) {
                ((ClassContainer) applicationArchive).addPackages(pkg.recursive(), getPackages(pkg));
            }
        }
    }

    private String getPackages(AddPackage pkg) {
        if (pkg.value().isEmpty()) {
            return pkg.containing().getPackage().getName();
        }

        return pkg.value();
    }

    private boolean canEnhanceWithPackages(Archive<?> applicationArchive, TestClass testClass) {
        return (applicationArchive instanceof ClassContainer)
                && testClass.getJavaClass().getAnnotationsByType(AddPackage.class).length > 0;
    }
}
