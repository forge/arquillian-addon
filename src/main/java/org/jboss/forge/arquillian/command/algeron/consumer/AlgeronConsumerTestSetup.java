package org.jboss.forge.arquillian.command.algeron.consumer;

import org.jboss.forge.arquillian.command.TestSetUp;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public interface AlgeronConsumerTestSetup extends TestSetUp {

    JavaClassSource updateTest(JavaClassSource test, String consumer, String provider, String fragmentName);

}
