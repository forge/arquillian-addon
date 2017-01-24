package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.lang.annotation.Annotation;

public class JavaSourceAssert extends AbstractAssert<JavaSourceAssert, JavaClassSource> {

    public JavaSourceAssert(JavaClassSource actual) {
        super(actual, JavaSourceAssert.class);
    }

    public static JavaSourceAssert assertThat(JavaClassSource source) {
        return new JavaSourceAssert(source);
    }

    public AnnotationAssert hasAnnotation(Class<? extends Annotation> annotation) {
        final AnnotationSource annotationSource = actual.getAnnotation(annotation);
        Assertions.assertThat(annotationSource).isNotNull();
        return new AnnotationAssert(annotationSource);
    }

    public MethodAssert hasMethod(String methodName) {
        final MethodSource<JavaClassSource> method = actual.getMethod(methodName);
        Assertions.assertThat(method).isNotNull();
        return new MethodAssert(method);
    }

    public JavaSourceAssert doesNotHaveMethod(String methodName) {
        final MethodSource<JavaClassSource> method = actual.getMethod(methodName);
        Assertions.assertThat(method).isNull();
        return this;
    }

    public FieldAssert hasField(String fieldName) {
        final FieldSource<JavaClassSource> field = actual.getField(fieldName);
        Assertions.assertThat(field).isNotNull();
        return new FieldAssert(field);
    }

    public JavaSourceAssert doesNotHaveField(String fieldName) {
        final FieldSource<JavaClassSource> field = actual.getField(fieldName);
        Assertions.assertThat(field).isNull();
        return this;
    }

    public JavaSourceAssert extendsClass(String className) {
        Assertions.assertThat(actual.getSuperType()).isEqualTo(className);
        return this;
    }
}
