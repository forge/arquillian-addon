package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.lang.annotation.Annotation;

public class FieldAssert extends AbstractAssert<FieldAssert, FieldSource<JavaClassSource>> {

    public FieldAssert(FieldSource<JavaClassSource> actual) {
        super(actual, FieldAssert.class);
    }

    public FieldAssert ofType(final String type) {
        Assertions.assertThat(actual.getType().getQualifiedName()).isEqualTo(type);
        return this;
    }

    public FieldAssert annotatedWith(final Class<? extends Annotation> annotationType) {
        Assertions.assertThat(actual.getAnnotation(annotationType)).isNotNull();
        return this;
    }

    public FieldAssert annotatedWith(final String annotation) {
        Assertions.assertThat(actual.getAnnotation(annotation)).isNotNull();
        return this;
    }

    public AnnotationAssert hasAnnotation(final String annotation) {

        final AnnotationSource<JavaClassSource> annotationSource = actual.getAnnotation(annotation);
        Assertions.assertThat(annotationSource).isNotNull();

        return new AnnotationAssert(annotationSource);
    }

    public FieldAssert annotatedWithStringValue(final String annotation, final String value) {
        final AnnotationSource<JavaClassSource> actualAnnotation = actual.getAnnotation(annotation);
        Assertions.assertThat(actualAnnotation).isNotNull();
        Assertions.assertThat(actualAnnotation.getStringValue()).isEqualTo(value);

        return this;
    }
}
