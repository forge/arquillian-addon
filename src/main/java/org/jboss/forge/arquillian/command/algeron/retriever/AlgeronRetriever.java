package org.jboss.forge.arquillian.command.algeron.retriever;

import org.jboss.forge.addon.ui.command.UICommand;

enum AlgeronRetriever {

    FOLDER(AlgeronAddFolderRetrieverCommand.class), URL(AlgeronAddUrlRetrieverCommand.class), GIT(AlgeronAddGitRetrieverCommand.class),
    MAVEN(AlgeronAddMavenRetrieverCommand.class), PACT_BROKER(AlgeronAddPactBrokerRetrieverCommand.class);

    private Class<? extends UICommand> implementingCommand;

    AlgeronRetriever(Class<? extends UICommand> implementingCommand) {
        this.implementingCommand = implementingCommand;
    }

    public Class<? extends UICommand> getImplementingCommand() {
        return implementingCommand;
    }

}
