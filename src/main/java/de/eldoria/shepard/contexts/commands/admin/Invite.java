package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import de.eldoria.shepard.messagehandler.ErrorType;
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
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__dd__I__nvite** -> Registers or update a invite" + lineSeparator()
                                + "**__rem__ove__I__nvite** -> removes a invite" + lineSeparator()
                                + "**__ref__resh__I__nvites** -> removes non present invites from database"
                                + lineSeparator()
                                + "**__s__how__I__nvites** -> Lists all registered invites", true),
                new CommandArg("values",
                        "**addInvite** -> [codeOfInvite] [Invite Name/Description]"
                                + lineSeparator()
                                + "**removeInvite** -> [codeOfInvite]" + lineSeparator()
                                + "**refreshInvites** -> leave empty" + lineSeparator()
                                + "**showInvites** -> leave empty", false)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("addInvite") || cmd.equalsIgnoreCase("ai")) {
            addInvite(args, receivedEvent);
            return;
        }
        if (cmd.equalsIgnoreCase("removeInvite") || cmd.equalsIgnoreCase("remi")) {
            removeInvite(args, receivedEvent);
            return;
        }
        if (cmd.equalsIgnoreCase("refreshInvites") || cmd.equalsIgnoreCase("refi")) {
            refreshInvites(receivedEvent);
            return;
        }
        if (cmd.equalsIgnoreCase("showInvites") ||cmd.equalsIgnoreCase("si")) {
            showInvites(receivedEvent);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void showInvites(MessageReceivedEvent receivedEvent) {
        List<DatabaseInvite> invites = InviteData.getInvites(receivedEvent.getGuild(), receivedEvent);
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
    }

    private void refreshInvites(MessageReceivedEvent receivedEvent) {
        InviteData.updateInvite(receivedEvent.getGuild(),
                receivedEvent.getGuild().retrieveInvites().complete(), receivedEvent);
        MessageSender.sendMessage("Removed non existent invites!", receivedEvent.getChannel());
    }

    private void removeInvite(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
            return;
        }
        List<DatabaseInvite> databaseInvites = InviteData.getInvites(receivedEvent.getGuild(), receivedEvent);
        for (DatabaseInvite invite : databaseInvites) {
            if (invite.getCode().equals(args[1])) {
                InviteData.removeInvite(receivedEvent.getGuild(), args[1], receivedEvent);
                MessageSender.sendMessage("Removed invite " + invite.getSource(), receivedEvent.getChannel());
            }
        }
        MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND,
                receivedEvent.getChannel());
    }

    private void addInvite(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, receivedEvent.getChannel());
            return;
        }
        List<net.dv8tion.jda.api.entities.Invite> invites = receivedEvent.getGuild().retrieveInvites().complete();
        for (var invite : invites) {
            if (invite.getCode().equals(args[1])) {
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                InviteData.addInvite(receivedEvent.getGuild(), invite.getCode(), name,
                        invite.getUses(), receivedEvent);
                MessageSender.sendMessage("Added Invite \"" + name + " with code " + invite.getCode()
                        + " to database with usage count of " + invite.getUses(), receivedEvent.getChannel());
                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, receivedEvent.getChannel());
    }
}
