package org.jboss.forge.arquillian.command.algeron.retriever;

import org.jboss.forge.addon.ui.command.UICommand;

enum AlgeronRetriever {

    FOLDER(AddAlgeronFolderRetriever.class), URL(AddAlgeronUrlRetriever.class), GIT(AddAlgeronGitRetriever.class),
    MAVEN(AddAlgeronMavenRetriever.class), PACT_BROKER(AddAlgeronPactBrokerRetriever.class);

    private Class<? extends UICommand> implementingCommand;

    AlgeronRetriever(Class<? extends UICommand> implementingCommand) {
        this.implementingCommand = implementingCommand;
    }

    public Class<? extends UICommand> getImplementingCommand() {
        return implementingCommand;
    }

}
