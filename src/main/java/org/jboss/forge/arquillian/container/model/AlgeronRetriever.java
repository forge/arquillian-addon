package org.jboss.forge.arquillian.container.model;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.arquillian.command.algeron.AddAlgeronFolderRetriever;
import org.jboss.forge.arquillian.command.algeron.AddAlgeronGitRetriever;
import org.jboss.forge.arquillian.command.algeron.AddAlgeronMavenRetriever;
import org.jboss.forge.arquillian.command.algeron.AddAlgeronPactBrokerRetriever;
import org.jboss.forge.arquillian.command.algeron.AddAlgeronUrlRetriever;

public enum AlgeronRetriever {

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
