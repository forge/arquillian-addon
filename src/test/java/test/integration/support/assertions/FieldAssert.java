package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
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
}
