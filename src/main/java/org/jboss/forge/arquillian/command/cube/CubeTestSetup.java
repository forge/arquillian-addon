package org.jboss.forge.arquillian.command.cube;

import org.jboss.forge.roaster.model.source.JavaClassSource;

public interface CubeTestSetup {

    JavaClassSource updateTest(JavaClassSource test);
}
