package test.integration.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddPackages {

    /**
     * List of packages to be automatically added
     * @return
     */
    String[] value();

    /**
     * Indicates if sub-packages should also be added
     */
    boolean recursive() default true;
}
