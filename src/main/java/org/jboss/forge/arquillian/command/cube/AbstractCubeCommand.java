package org.jboss.forge.arquillian.command.cube;


import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;

import javax.inject.Inject;

public abstract class AbstractCubeCommand extends AbstractProjectCommand {

    @Inject
    private ProjectFactory projectFactory;

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

}
