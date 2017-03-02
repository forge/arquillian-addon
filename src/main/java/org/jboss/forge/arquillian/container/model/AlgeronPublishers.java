package org.jboss.forge.arquillian.container.model;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.arquillian.command.algeron.publisher.AddAlgeronFolderPublisher;
import org.jboss.forge.arquillian.command.algeron.publisher.AddAlgeronGitPublisher;
import org.jboss.forge.arquillian.command.algeron.publisher.AddAlgeronUrlPublisher;

public enum AlgeronPublishers {

    FOLDER(AddAlgeronFolderPublisher.class), URL(AddAlgeronUrlPublisher.class), GIT(AddAlgeronGitPublisher.class);

    private Class<? extends UICommand> implementingCommand;

    AlgeronPublishers(Class<? extends UICommand> implementingCommand) {
        this.implementingCommand = implementingCommand;
    }

    public Class<? extends UICommand> getImplementingCommand() {
        return implementingCommand;
    }
}
