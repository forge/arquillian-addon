package org.jboss.forge.arquillian.command.cube;

import org.jboss.forge.arquillian.command.TestSetUp;
import org.jboss.forge.roaster.model.source.JavaClassSource;

interface CubeTestSetup extends TestSetUp {

    JavaClassSource updateTest(JavaClassSource test);
}
