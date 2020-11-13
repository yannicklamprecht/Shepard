package de.eldoria.shepard.commandmodules.changelog;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command which configure the logging channel.
 * Will grow when the bot receives more moderation features.
 * Similar to AuditLog, but can also log bot actions in the future.
 * The logging events are present in {@link ChangelogListener}.
 */
@CommandUsage(EventContext.GUILD)
public class Changelog extends Command implements Executable, ReqParser, ReqDataSource {
    private ArgumentParser parser;
    private ChangelogData changelogData;

    /**
     * Creates a new changelog command object.
     */
    public Changelog() {
        super("changelog",
                new String[]{"log"},
                "command.changelog.description",
                SubCommand.builder("changelog")
                        .addSubcommand("command.changelog.subcommand.addRole",
                                Parameter.createCommand("addRole"),
                                Parameter.createInput("words.role", "command.general.argumentDescription.role", true))
                        .addSubcommand("command.changelog.subcommand.removeRole",
                                Parameter.createCommand("removeRole"),
                                Parameter.createInput("words.role", "command.general.argumentDescription.role", true))
                        .addSubcommand("command.changelog.subcommand.activate",
                                Parameter.createCommand("activate"),
                                Parameter.createInput("command.general.argument.channel", "command.general.argumentDescription.channelMentionOrExecution", false))
                        .addSubcommand("command.changelog.subcommand.deactivate",
                                Parameter.createCommand("deactivate"))
                        .addSubcommand("command.changelog.subcommand.roles",
                                Parameter.createCommand("roles"))
                        .build(),
                CommandCategory.ADMIN
        );
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0) || isSubCommand(cmd, 1)) {
            modifyRoles(args, wrapper, cmd);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            activate(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            deactivate(wrapper);
            return;
        }

        if (isSubCommand(cmd, 4)) {
            showRoles(wrapper);
            return;
        }
    }

    private void showRoles(EventWrapper messageContext) {
        List<String> collect = parser.getRoles(messageContext.getGuild().get(),
                changelogData.getRoles(messageContext.getGuild().get(), messageContext))
                .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

        MessageSender.sendSimpleTextBox(M_LOGGED_ROLES.tag,
                String.join(lineSeparator(), collect), messageContext);
    }

    private void deactivate(EventWrapper messageContext) {
        if (changelogData.removeChannel(messageContext.getGuild().get(), messageContext)) {
            MessageSender.sendMessage(M_DEACTIVATED.tag, messageContext.getMessageChannel());
        }
    }

    private void activate(String[] args, EventWrapper messageContext) {
        Optional<TextChannel> textChannel;
        if (args.length == 1) {
            textChannel = messageContext.getTextChannel();
        } else {
            textChannel = ArgumentParser.getTextChannel(messageContext.getGuild().get(), args[1]);
        }


        if (textChannel.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext);
            return;
        }

        if (changelogData.setChannel(messageContext.getGuild().get(), textChannel.get(), messageContext)) {
            MessageSender.sendMessage(M_ACTIVATED + " " + textChannel.get().getAsMention(),
                    messageContext.getMessageChannel());
        }
    }

    private void modifyRoles(String[] args, EventWrapper messageContext, String cmd) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        Optional<Role> role = parser.getRole(messageContext.getGuild().get(), args[1]);

        if (role.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, messageContext);
            return;
        }

        if (isSubCommand(cmd, 0)) {
            if (changelogData.addRole(messageContext.getGuild().get(), role.get(), messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_ADDED_ROLE.tag,
                        messageContext,
                        "**" + role.get().getName() + "**"), messageContext.getMessageChannel());
            }
        } else {
            if (changelogData.removeRole(messageContext.getGuild().get(), role.get(), messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_REMOVED_ROLE.tag, messageContext,
                        "**" + role.get().getName() + "**"), messageContext.getMessageChannel());
            }
        }
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDataSource(DataSource source) {
        changelogData = new ChangelogData(source);
    }
}
