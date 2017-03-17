package org.jboss.forge.arquillian.command;


import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

public interface TestSetUp {

    default void removeTestMethod(String methodName, JavaClassSource test) {
        final MethodSource<JavaClassSource> should_be_deployed = test.getMethod(methodName);
        if (test.hasMethod(should_be_deployed)) {
            test.removeMethod(should_be_deployed);
        }
    }
}
