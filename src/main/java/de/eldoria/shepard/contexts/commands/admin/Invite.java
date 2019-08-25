package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.Invites;
import de.eldoria.shepard.database.types.DatabaseInvite;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.lineSeparator;

public class Invite extends Command {
    /**
     * Creates a new Invite command object.
     */
    public Invite() {
        commandName = "invite";
        commandDesc = "Manage registered invites";
        arguments = new CommandArg[] {new CommandArg("action",
                "**addInvite** -> Registers or update a invite" + lineSeparator()
                        + "**removeInvite** -> removes a invite" + lineSeparator()
                        + "**refreshInvites** -> removes non present invites from database" + lineSeparator()
                        + "**showInvites** -> Lists all registered invites", true),
                new CommandArg("values", "**addInvite** -> [codeOfInvite] [Invite Name/Description]"
                        + lineSeparator()
                        + "**removeInvite** -> [codeOfInvite]" + lineSeparator()
                        + "**refreshInvites** -> leave empty" + lineSeparator()
                        + "**showInvites** -> leave empty", false)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        if (args[0].equalsIgnoreCase("addInvite")) {
            addInvite(args, receivedEvent);
            return;
        }
        if (args[0].equalsIgnoreCase("removeInvite")) {
            removeInvite(args, receivedEvent);
            return;
        }
        if (args[0].equalsIgnoreCase("refreshInvites")) {
            refreshInvites(receivedEvent);
            return;
        }
        if (args[0].equalsIgnoreCase("showInvites")) {
            showInvites(receivedEvent);
            return;
        }
        MessageSender.sendSimpleError("Invalid argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void showInvites(MessageReceivedEvent receivedEvent) {
        List<DatabaseInvite> invites = Invites.getInvites(receivedEvent.getGuild(), receivedEvent);
        StringBuilder message = new StringBuilder();
        String code = "code      ";
        String usages = "Usage Count";
        String name = "Name";
        message.append("Registered Invites: ").append(lineSeparator())
                .append("```yaml").append(lineSeparator())
                .append(code).append(usages).append(name);

        for (DatabaseInvite invite : invites) {
            String invCode = StringUtils.rightPad(invite.getCode(), code.length(), " ");
            String invUsage = StringUtils.rightPad(invite.getUsedCount() + "", usages.length(), " ");
            String invName = invite.getSource();
            message.append(invCode).append(invUsage).append(invName).append(lineSeparator());
        }
        message.append("```");
        MessageSender.sendMessage(message.toString(), receivedEvent.getChannel());
        return;
    }

    private void refreshInvites(MessageReceivedEvent receivedEvent) {
        Invites.updateInvite(receivedEvent.getGuild(),
                receivedEvent.getGuild().retrieveInvites().complete(), receivedEvent);
        MessageSender.sendMessage("Removed non existent invites!", receivedEvent.getChannel());
    }

    private void removeInvite(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length != 2) {
            MessageSender.sendSimpleError("Invalid Argument", receivedEvent.getChannel());
            return;
        }
        List<DatabaseInvite> databaseInvites = Invites.getInvites(receivedEvent.getGuild(), receivedEvent);
        for (DatabaseInvite invite : databaseInvites) {
            if (invite.getCode().equals(args[1])) {
                Invites.removeInvite(receivedEvent.getGuild(), args[1], receivedEvent);
                MessageSender.sendMessage("Removed invite " + invite.getSource(), receivedEvent.getChannel());
            }
        }
        MessageSender.sendSimpleError("No registered invite with code \"" + args[1] + "\" found",
                receivedEvent.getChannel());
    }

    private void addInvite(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length < 3) {
            MessageSender.sendSimpleError("Too few Arguments for add invite", receivedEvent.getChannel());
            return;
        }
        List<net.dv8tion.jda.api.entities.Invite> invites = receivedEvent.getGuild().retrieveInvites().complete();
        for (var invite : invites) {
            if (invite.getCode().equals(args[1])) {
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                Invites.addInvite(receivedEvent.getGuild(), invite.getCode(), name,
                        invite.getUses(), receivedEvent);
                MessageSender.sendMessage("Added Invite \"" + name + " with code " + invite.getCode()
                        + " to database with usage count of " + invite.getUses(), receivedEvent.getChannel());
                return;
            }
        }
        MessageSender.sendSimpleError("No invite with code " + args[1] + " found!", receivedEvent.getChannel());
    }
}
