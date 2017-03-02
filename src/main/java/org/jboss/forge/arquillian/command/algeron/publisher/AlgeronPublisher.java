package org.jboss.forge.arquillian.command.algeron.publisher;

import org.jboss.forge.addon.ui.command.UICommand;

enum AlgeronPublisher {

    FOLDER(AddAlgeronFolderPublisher.class), URL(AddAlgeronUrlPublisher.class), GIT(AddAlgeronGitPublisher.class);

    private Class<? extends UICommand> implementingCommand;

    AlgeronPublisher(Class<? extends UICommand> implementingCommand) {
        this.implementingCommand = implementingCommand;
    }

    public Class<? extends UICommand> getImplementingCommand() {
        return implementingCommand;
    }
}
