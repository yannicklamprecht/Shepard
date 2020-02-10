package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.ChangelogData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.*;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command which configure the logging channel.
 * Will grow when the bot receives more moderation features.
 * Similar to AuditLog, but can also log bot actions in the future.
 * The logging events are present in {@link de.eldoria.shepard.listener.ChangelogListener}.
 */
public class Changelog extends Command {
    /**
     * Creates a new changelog command object.
     */
    public Changelog() {
        commandName = "changelog";
        commandAliases = new String[] {"log"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("addRole", C_ADD_ROLE.tag, true),
                        new SubArgument("removeRole", C_REMOVE_ROLE.tag, true),
                        new SubArgument("activate", C_ACTIVATE.tag, true),
                        new SubArgument("deactivate", C_DEACTIVATE.tag, true),
                        new SubArgument("roles", C_ROLES.tag, true)),
                new CommandArgument("value", false,
                        new SubArgument("addRole", A_ROLE.tag),
                        new SubArgument("removeRole", A_ROLE.tag),
                        new SubArgument("activate", A_CHANNEL.tag),
                        new SubArgument("deactivate", A_EMPTY.tag),
                        new SubArgument("roles", A_EMPTY.tag))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArgument arg = commandArguments[0];
        if (arg.isSubCommand(cmd, 0) || arg.isSubCommand(cmd, 1)) {
            modifyRoles(args, messageContext, cmd);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            activate(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            deactivate(messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 4)) {
            showRoles(messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void showRoles(MessageEventDataWrapper messageContext) {
        List<String> collect = ArgumentParser.getRoles(messageContext.getGuild(),
                ChangelogData.getRoles(messageContext.getGuild(), messageContext))
                .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

        MessageSender.sendSimpleTextBox(M_LOGGED_ROLES.tag,
                String.join(lineSeparator(), collect), messageContext.getTextChannel());
    }

    private void deactivate(MessageEventDataWrapper messageContext) {
        if (ChangelogData.removeChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage(M_DEACTIVATED.tag,
                    messageContext.getTextChannel());
        }
    }

    private void activate(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        TextChannel textChannel = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

        if (textChannel == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext.getTextChannel());
            return;
        }

        if (ChangelogData.setChannel(messageContext.getGuild(), textChannel, messageContext)) {
            MessageSender.sendMessage(M_ACTIVATED + " " + textChannel.getAsMention(), messageContext.getTextChannel());
        }
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext, String cmd) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Role role = ArgumentParser.getRole(messageContext.getGuild(), args[1]);

        if (role == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, messageContext.getTextChannel());
            return;
        }
        CommandArgument arg = commandArguments[0];

        if (arg.isSubCommand(cmd, 0)) {
            if (ChangelogData.addRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_ADDED_ROLE.tag,
                        messageContext.getGuild(),
                        "**" + role.getName() + "**"), messageContext.getTextChannel());
            }
        } else {
            if (ChangelogData.removeRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_REMOVED_ROLE.tag, messageContext.getGuild(),
                        "**" + role.getName() + "**"), messageContext.getTextChannel());
            }
        }
    }
}
