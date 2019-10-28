package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ChangelogData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.C_ACTIVATE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.C_ADD_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.C_DEACTIVATE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.C_REMOVE_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.C_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.M_ACTIVATED;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.M_ADDED_ROLE;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.M_DEACTIVATED;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.M_LOGGED_ROLES;
import static de.eldoria.shepard.localization.enums.commands.admin.ChangelogLocale.M_REMOVED_ROLE;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Changelog extends Command {
    /**
     * Creates a new changelog command object.
     */
    public Changelog() {
        commandName = "changelog";
        commandAliases = new String[] {"log"};
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("addRole", C_ADD_ROLE.tag, true),
                        new SubArg("removeRole", C_REMOVE_ROLE.tag, true),
                        new SubArg("activate", C_ACTIVATE.tag, true),
                        new SubArg("deactivate", C_DEACTIVATE.tag, true),
                        new SubArg("roles", C_ROLES.tag, true)),
                new CommandArg("value", false,
                        new SubArg("addRole", A_ROLE.tag),
                        new SubArg("removeRole", A_ROLE.tag),
                        new SubArg("activate", A_CHANNEL.tag),
                        new SubArg("deactivate", A_EMPTY.tag),
                        new SubArg("roles", A_EMPTY.tag))
        };

        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "addrole", "ar", "removeRole", "rr")) {
            modifyRoles(args, messageContext, cmd);
            return;
        }

        if (isArgument(cmd, "activate, a")) {
            activate(args, messageContext);
            return;
        }

        if (isArgument(cmd, "deactivate", "d")) {
            deactivate(messageContext);
            return;
        }

        if (isArgument(cmd, "roles", "r")) {
            showRoles(messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void showRoles(MessageEventDataWrapper receivedEvent) {
        List<String> collect = ArgumentParser.getRoles(receivedEvent.getGuild(),
                ChangelogData.getRoles(receivedEvent.getGuild(), receivedEvent))
                .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

        MessageSender.sendSimpleTextBox(M_LOGGED_ROLES.tag,
                String.join(lineSeparator(), collect), receivedEvent);
    }

    private void deactivate(MessageEventDataWrapper receivedEvent) {
        if (ChangelogData.removeChannel(receivedEvent.getGuild(), receivedEvent)) {
            MessageSender.sendMessage(M_DEACTIVATED.tag,
                    receivedEvent);
        }
    }

    private void activate(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        TextChannel textChannel = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

        if (textChannel == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext);
            return;
        }

        if (ChangelogData.setChannel(messageContext.getGuild(), textChannel, messageContext)) {
            MessageSender.sendMessage(M_ACTIVATED + " " + textChannel.getAsMention(),
                    messageContext);
        }
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext, String cmd) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        Role role = ArgumentParser.getRole(messageContext.getGuild(), args[1]);

        if (role == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, messageContext);
            return;
        }

        if (isArgument(cmd, "addRole", "ar")) {
            if (ChangelogData.addRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage(locale.getReplacedString(M_ADDED_ROLE.localeCode,
                        messageContext.getGuild(),
                        "**" + role.getName() + "**"),
                        messageContext);
            }
        } else {
            if (ChangelogData.removeRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage(locale.getReplacedString(M_REMOVED_ROLE.localeCode, messageContext.getGuild(),
                        "**" + role.getName() + "**"),
                        messageContext);
            }
        }
    }
}
