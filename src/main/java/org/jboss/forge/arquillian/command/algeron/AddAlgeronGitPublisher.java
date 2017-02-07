package org.jboss.forge.arquillian.command.algeron;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddAlgeronGitPublisher extends AbstractAlgeronPublisherCommand {

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    @WithAttributes(shortName = 'u', label = "Git Url", required = true)
    private UIInput<String> url;

    @Inject
    @WithAttributes(shortName = 'm', label = "Comment", required = true)
    private UIInput<String> comment;

    @Inject
    @WithAttributes(shortName = 's', label = "Username")
    private UIInput<String> username;

    @Inject
    @WithAttributes(shortName = 'w', label = "Password", type = InputType.SECRET)
    private UIInput<String> password;

    @Inject
    @WithAttributes(shortName = 'h', label = "Passphrase", type = InputType.SECRET)
    private UIInput<String> passphrase;

    @Inject
    @WithAttributes(shortName = 'k', label = "Key Location", type = InputType.FILE_PICKER)
    private UIInput<String> key;

    @Inject
    @WithAttributes(shortName = 'r', label = "Remote", type = InputType.FILE_PICKER)
    private UIInput<String> remote;

    @Inject
    @WithAttributes(shortName = 'o', label = "Repository")
    private UIInput<String> repository;

    @Inject
    @WithAttributes(shortName = 'd', label = "Contracts Directory")
    private UIInput<String> contractGitDirectory;

    @Inject
    @WithAttributes(shortName = 't', label = "Tag")
    private UIInput<String> tag;

    @Inject
    @WithAttributes(shortName = 'b', label = "Branch")
    private UIInput<String> branch;

    @Inject
    @WithAttributes(shortName = 'e', label = "Email")
    private UIInput<String> email;

    @Inject
    @WithAttributes(shortName = 'c', label = "Contracts Folder")
    private UIInput<String> contractFolder;

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.from(super.getMetadata(context), getClass())
            .category(Categories.create("Algeron"))
            .name("Arquillian Algeron: Add Git Publisher")
            .description("This command registers a Git Publisher for Algeron");
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(url).add(contractFolder)
            .add(comment).add(username)
            .add(password).add(passphrase)
            .add(key).add(remote)
            .add(repository).add(contractGitDirectory)
            .add(tag).add(branch)
            .add(email);
        contractFolder.setDefaultValue("target/pacts");
        remote.setDefaultValue("origin");
    }

    @Override
    protected DependencyBuilder getPublisherDependency() {
        return DependencyBuilder.create().setGroupId("org.arquillian.universe")
            .setArtifactId("arquillian-algeron-git-publisher").setPackaging("pom").setScopeType("test");
    }

    @Override
    protected Map<String, String> getParameters() {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("provider", "git");
        parameters.put("url", url.getValue());
        parameters.put("comment", comment.getValue());
        parameters.put("remote", remote.getValue());
        parameters.put("contractsFolder", contractFolder.getValue());

        if (username.hasValue()) {
            parameters.put("username", username.getValue());
        }

        if (password.hasValue()) {
            parameters.put("password", password.getValue());
        }

        if (passphrase.hasValue()) {
            parameters.put("passphrase", passphrase.getValue());
        }

        if (key.hasValue()) {
            parameters.put("key", key.getValue());
        }

        if (repository.hasValue()) {
            parameters.put("repository", repository.getValue());
        }

        if (contractGitDirectory.hasValue()) {
            parameters.put("contractGitDirectory", contractGitDirectory.getValue());
        }

        if (tag.hasValue()) {
            parameters.put("tag", tag.getValue());
        }

        if (branch.hasValue()) {
            parameters.put("branch", branch.getValue());
        }

        if (email.hasValue()) {
            parameters.put("email", email.getValue());
        }

        return parameters;
    }

    @Override
    protected String getName() {
        return "Git";
    }
}
