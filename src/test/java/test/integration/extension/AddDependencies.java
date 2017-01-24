package test.integration.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines set of dependencies which will be added to deployed test archive.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddDependencies {

    /**
     * List of maven artifact coordinates in the format of groupId:artifactId:version.
     * @return
     */
    String[] value();
}
