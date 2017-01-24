package test.integration.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AddPackages.class)
public @interface AddPackage {

    /**
     * Fully qualified package name to be automatically added, Can be used interchangeable with {@link #containing()}. Does take precedence.
     */
    String value() default "";

    /**
     * Class defining a root package. Can be used interchangeable with {@link #value()}. Does not take precedence
     */
    Class<?> containing() default AddPackage.class;

    /**
     * Indicates if sub-packages should also be added
     */
    boolean recursive() default true;
}
