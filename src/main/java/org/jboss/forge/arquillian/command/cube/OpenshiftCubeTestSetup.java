package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.roaster.model.source.JavaClassSource;

public class OpenshiftCubeTestSetup implements CubeTestSetup {

    // TODO: 2/27/17 Need to add logic once we have openshift fest example for standalone.
    // https://github.com/arquillian/arquillian-cube/issues/608

    @Override
    public JavaClassSource updateTest(JavaClassSource test) {
        return null;
    }
}
