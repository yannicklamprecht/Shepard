package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.ChangelogData;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.DbUtil.getIdRaw;
import static java.lang.System.lineSeparator;

public class Changelog extends Command {

    /**
     * Creates a new changelog command object.
     */
    public Changelog() {
        commandName = "changelog";
        commandAliases = new String[] {"log"};
        commandDesc = "provides function to log role changes on a guild";
        arguments = new CommandArg[] {
                new CommandArg("action",
                        "**addRole** -> Adds a role to changelog" + lineSeparator()
                                + "**removeRole** -> Removes a role from changelog" + lineSeparator()
                                + "**activate** -> Activates changelog posting in a channel."
                                + "Overrides old channel if set." + lineSeparator()
                                + "**deactivate** -> Deactivates changelog postings" + lineSeparator()
                                + "**roles** -> Shows currently observed roles", true),
                new CommandArg("value",
                        "**addRole** -> [role]" + lineSeparator()
                                + "**removeRole** -> [role]" + lineSeparator()
                                + "**activate** -> [channel]" + lineSeparator()
                                + "**deactivate** -> leave empty" + lineSeparator()
                                + "**roles** -> leave empty", false)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("addRole") || cmd.equalsIgnoreCase("removeRole")) {
            modifyRoles(args, receivedEvent, cmd);
            return;
        }

        if (cmd.equalsIgnoreCase("activate")) {
            activate(args, receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("deactivate")) {
            deactivate(receivedEvent);
            return;
        }

        if (cmd.equalsIgnoreCase("roles")) {
            showRoles(receivedEvent);
            return;
        }

        MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void showRoles(MessageReceivedEvent receivedEvent) {
        List<String> roleIds = ChangelogData.getRoles(receivedEvent.getGuild(), receivedEvent);

        List<Role> validRoles = Verifier.getValidRoles(receivedEvent.getGuild(), roleIds);

        List<String> roleMentions = roleIds.stream()
                .map(roleId -> receivedEvent.getGuild().getRoleById(getIdRaw(roleId)))
                .filter(Objects::nonNull).map(IMentionable::getAsMention)
                .collect(Collectors.toList());

        MessageSender.sendSimpleTextBox("Currently logged roles:",
                String.join(lineSeparator(), roleMentions), receivedEvent.getChannel());
    }

    private void deactivate(MessageReceivedEvent receivedEvent) {
        ChangelogData.removeChannel(receivedEvent.getGuild(), receivedEvent);

        MessageSender.sendMessage("Changelog is deactivated", receivedEvent.getChannel());
    }

    private void activate(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length != 2) {
            MessageSender.sendSimpleError("Invalid Arguments", receivedEvent.getChannel());
            return;
        }

        TextChannel textChannelById = receivedEvent.getGuild().getTextChannelById(getIdRaw(args[1]));

        if (textChannelById == null) {
            MessageSender.sendSimpleError("Invalid Channel.", receivedEvent.getChannel());
            return;
        }

        ChangelogData.setChannel(receivedEvent.getGuild(), textChannelById, receivedEvent);

        MessageSender.sendMessage("Changelog is presented in channel" + textChannelById.getAsMention(),
                receivedEvent.getChannel());
    }

    private void modifyRoles(String[] args, MessageReceivedEvent receivedEvent, String cmd) {
        if (args.length != 2) {
            MessageSender.sendSimpleError("Invalid Arguments", receivedEvent.getChannel());
            return;
        }

        Role roleById = receivedEvent.getGuild().getRoleById(getIdRaw(args[1]));
        if (roleById == null) {
            MessageSender.sendSimpleError("Invalid Group.", receivedEvent.getChannel());
            return;
        }

        if (cmd.equalsIgnoreCase("addRole")) {
            ChangelogData.addRole(receivedEvent.getGuild(), roleById, receivedEvent);
            MessageSender.sendMessage("Added role **" + roleById.getName() + "** to changelog.",
                    receivedEvent.getChannel());
        } else {
            ChangelogData.removeRole(receivedEvent.getGuild(), roleById, receivedEvent);
            MessageSender.sendMessage("Removed role **" + roleById.getName() + "** from changelog.",
                    receivedEvent.getChannel());
        }
    }
}
