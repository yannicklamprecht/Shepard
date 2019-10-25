package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.ChangelogData;
import de.eldoria.shepard.localization.enums.ChangelogLocale;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Changelog extends Command {
    /**
     * Creates a new changelog command object.
     */
    public Changelog() {
        commandName = "changelog";
        commandAliases = new String[] {"log"};
        commandDesc = "provides function to log role changes on a guild";
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("addRole", ChangelogLocale.C_ADD_ROLE.replacement, true),
                        new SubArg("removeRole", ChangelogLocale.C_REMOVE_ROLE.replacement, true),
                        new SubArg("activate", ChangelogLocale.C_ACTIVATE.replacement, true),
                        new SubArg("deactivate", ChangelogLocale.C_DEACTIVATE.replacement, true),
                        new SubArg("roles", ChangelogLocale.C_ROLES.replacement, true)),
                new CommandArg("value", false,
                        new SubArg("addRole", GeneralLocale.ROLE.replacement),
                        new SubArg("removeRole", GeneralLocale.ROLE.replacement),
                        new SubArg("activate", GeneralLocale.CHANNEL.replacement),
                        new SubArg("deactivate", GeneralLocale.EMPTY.replacement),
                        new SubArg("roles", GeneralLocale.EMPTY.replacement))};

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

        MessageSender.sendSimpleTextBox(ChangelogLocale.M_LOGGED_ROLES.replacement,
                String.join(lineSeparator(), collect), receivedEvent);
    }

    private void deactivate(MessageEventDataWrapper receivedEvent) {
        if (ChangelogData.removeChannel(receivedEvent.getGuild(), receivedEvent)) {
            MessageSender.sendMessage(ChangelogLocale.M_DEACTIVATED.replacement,
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
            MessageSender.sendMessage(ChangelogLocale.M_ACTIVATED + " " + textChannel.getAsMention(),
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
                MessageSender.sendMessage(locale.getReplacedString(ChangelogLocale.M_ADDED_ROLE.localeCode,
                        messageContext.getGuild(),
                        "**" + role.getName() + "**"),
                        messageContext);
            }
        } else {
            if (ChangelogData.removeRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage(locale.getReplacedString(ChangelogLocale.M_ADDED_ROLE.localeCode, messageContext.getGuild(),
                        "**" + role.getName() + "**"),
                        messageContext);
            }
        }
    }
}
