package org.jboss.forge.arquillian.command.algeron.publisher;

import org.jboss.forge.addon.ui.command.UICommand;

enum AlgeronPublishers {

    FOLDER(AddAlgeronFolderPublisher.class), URL(AddAlgeronUrlPublisher.class), GIT(AddAlgeronGitPublisher.class);

    private Class<? extends UICommand> implementingCommand;

    AlgeronPublishers(Class<? extends UICommand> implementingCommand) {
        this.implementingCommand = implementingCommand;
    }

    public Class<? extends UICommand> getImplementingCommand() {
        return implementingCommand;
    }
}
