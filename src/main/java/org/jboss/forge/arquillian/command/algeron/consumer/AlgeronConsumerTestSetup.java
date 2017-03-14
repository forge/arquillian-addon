package org.jboss.forge.arquillian.command.algeron.consumer;

import org.jboss.forge.roaster.model.source.JavaClassSource;

public interface AlgeronConsumerTestSetup {

    JavaClassSource updateTest(JavaClassSource test, String consumer, String provider, String fragmentName);

}
