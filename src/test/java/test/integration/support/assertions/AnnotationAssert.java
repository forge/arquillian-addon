package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.roaster.model.source.AnnotationSource;

public class AnnotationAssert extends AbstractAssert<AnnotationAssert, AnnotationSource> {

    public AnnotationAssert(AnnotationSource actual) {
        super(actual, AnnotationAssert.class);
    }

    public static AnnotationAssert assertThat(AnnotationSource source) {
        return new AnnotationAssert(source);
    }

    public AnnotationAssert withValue(String value) {
        Assertions.assertThat(actual.getStringValue()).isEqualTo(value);
        return this;
    }

    public AnnotationAssert withEntry(String entry, String value) {
        Assertions.assertThat(actual.getStringValue(entry)).isEqualTo(value);
        return this;
    }
}
