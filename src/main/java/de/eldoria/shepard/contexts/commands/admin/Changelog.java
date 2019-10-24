package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.ChangelogData;
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
                new CommandArg("action",
                        "**__a__dd__R__ole** -> Adds a role to changelog" + lineSeparator()
                                + "**__r__emove__R__ole** -> Removes a role from changelog" + lineSeparator()
                                + "**__a__ctivate** -> Activates changelog posting in a channel."
                                + "Overrides old channel if set." + lineSeparator()
                                + "**__d__eactivate** -> Deactivates changelog postings" + lineSeparator()
                                + "**__r__oles** -> Shows currently observed roles", true),
                new CommandArg("value",
                        "**addRole** -> [role]" + lineSeparator()
                                + "**removeRole** -> [role]" + lineSeparator()
                                + "**activate** -> [channel]" + lineSeparator()
                                + "**deactivate** -> leave empty" + lineSeparator()
                                + "**roles** -> leave empty", false)};
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

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void showRoles(MessageEventDataWrapper receivedEvent) {
        List<String> collect = ArgumentParser.getRoles(receivedEvent.getGuild(),
                ChangelogData.getRoles(receivedEvent.getGuild(), receivedEvent))
                .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

        MessageSender.sendSimpleTextBox("Currently logged roles:",
                String.join(lineSeparator(), collect), receivedEvent.getChannel());
    }

    private void deactivate(MessageEventDataWrapper receivedEvent) {
        if (ChangelogData.removeChannel(receivedEvent.getGuild(), receivedEvent)) {
            MessageSender.sendMessage("Changelog is deactivated", receivedEvent.getChannel());
        }
    }

    private void activate(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }

        TextChannel textChannelById = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

        if (textChannelById == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext.getChannel());
            return;
        }

        if (ChangelogData.setChannel(messageContext.getGuild(), textChannelById, messageContext)) {
            MessageSender.sendMessage("Changelog is presented in channel" + textChannelById.getAsMention(),
                    messageContext.getChannel());
        }
    }

    private void modifyRoles(String[] args, MessageEventDataWrapper messageContext, String cmd) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }

        Role role = ArgumentParser.getRole(messageContext.getGuild(), args[1]);

        if (role == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "addRole", "ar")) {
            if (ChangelogData.addRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage("Added role **" + role.getName() + "** to changelog.",
                        messageContext.getChannel());
            }
        } else {
            if (ChangelogData.removeRole(messageContext.getGuild(), role, messageContext)) {
                MessageSender.sendMessage("Removed role **" + role.getName() + "** from changelog.",
                        messageContext.getChannel());
            }
        }
    }
}
