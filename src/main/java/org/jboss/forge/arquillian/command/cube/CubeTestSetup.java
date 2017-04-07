package org.jboss.forge.arquillian.command.cube;

import org.jboss.forge.roaster.model.source.JavaClassSource;

interface CubeTestSetup {

    JavaClassSource updateTest(JavaClassSource test);
}
