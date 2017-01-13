package test.integration.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.lang.annotation.Annotation;

public class MethodAssert extends AbstractAssert<MethodAssert, MethodSource> {

   public MethodAssert(MethodSource actual) {
      super(actual, MethodAssert.class);
   }

   public static MethodAssert assertThat(MethodSource source) {
      return new MethodAssert(source);
   }

   public AnnotationAssert withAnnotation(Class<? extends Annotation> annotation) {
      final AnnotationSource annotationSource = actual.getAnnotation(annotation);
      Assertions.assertThat(annotationSource).isNotNull();
      return new AnnotationAssert(annotationSource);
   }
}
