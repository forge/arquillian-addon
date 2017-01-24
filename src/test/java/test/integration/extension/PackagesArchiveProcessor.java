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
            final AddPackages[] packages = testClass.getJavaClass().getAnnotationsByType(AddPackages.class);
            for (AddPackages pkg : packages) {
                ((ClassContainer) applicationArchive).addPackages(pkg.recursive(), getPackages(pkg));
            }
        }

    }

    private String[] getPackages(AddPackages pkg) {
        if (pkg.value().length == 1 && pkg.value()[0].isEmpty()) {
            return new String[]{pkg.containing().getPackage().getName()};
        }

        return pkg.value();
    }

    private boolean canEnhanceWithPackages(Archive<?> applicationArchive, TestClass testClass) {
        return (applicationArchive instanceof ClassContainer)
                && testClass.getJavaClass().getAnnotationsByType(AddPackages.class).length > 0;
    }
}
