package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.InviteData;
import de.eldoria.shepard.database.types.DatabaseInvite;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;

public class Invite extends Command {

    private final Pattern pattern;

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

        pattern = Pattern.compile("([a-zA-Z0-9]{6,7})$");
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("addInvite") || cmd.equalsIgnoreCase("ai")) {
            addInvite(args, messageContext);
            return;
        }
        if (cmd.equalsIgnoreCase("removeInvite") || cmd.equalsIgnoreCase("remi")) {
            removeInvite(args, messageContext);
            return;
        }
        if (cmd.equalsIgnoreCase("refreshInvites") || cmd.equalsIgnoreCase("refi")) {
            refreshInvites(messageContext);
            return;
        }
        if (cmd.equalsIgnoreCase("showInvites") || cmd.equalsIgnoreCase("si")) {
            showInvites(messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
        sendCommandUsage(messageContext.getChannel());
    }

    private void showInvites(MessageEventDataWrapper messageContext) {
        List<DatabaseInvite> invites = InviteData.getInvites(messageContext.getGuild(), messageContext);

        StringBuilder message = new StringBuilder();
        String code = "code      ";
        String usages = "Usage Count ";
        String name = "Name";
        message.append("Registered Invites: ").append(lineSeparator())
                .append("```yaml").append(lineSeparator())
                .append(code).append(usages).append(name).append(lineSeparator());

        for (DatabaseInvite invite : invites) {
            String invCode = StringUtils.rightPad(invite.getCode(), code.length(), " ");
            String invUsage = StringUtils.rightPad(invite.getUsedCount() + "", usages.length(), " ");
            String invName = invite.getSource();
            message.append(invCode).append(invUsage).append(invName).append(lineSeparator());
        }
        message.append("```");
        MessageSender.sendMessage(message.toString(), messageContext.getChannel());
    }

    private void refreshInvites(MessageEventDataWrapper messageContext) {
        messageContext.getGuild().retrieveInvites().queue(invites -> {
            if (InviteData.updateInvite(messageContext.getGuild(), invites, messageContext)) {
                MessageSender.sendMessage("Removed non existent invites!", messageContext.getChannel());
            }
        });
    }

    private void removeInvite(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
            return;
        }
        List<DatabaseInvite> databaseInvites = InviteData.getInvites(receivedEvent.getGuild(), receivedEvent);

        for (DatabaseInvite invite : databaseInvites) {
            if (invite.getCode().equals(args[1])) {
                if (InviteData.removeInvite(receivedEvent.getGuild(), args[1], receivedEvent)) {
                    MessageSender.sendMessage("Removed invite " + invite.getSource(), receivedEvent.getChannel());
                    return;
                }
            }
        }
        MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND,
                receivedEvent.getChannel());
    }

    private void addInvite(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        Matcher matcher = pattern.matcher(args[1]);
        if (!matcher.find()) {
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, messageContext.getChannel());
        }
        String code = matcher.group(1);


        messageContext.getGuild().retrieveInvites().queue(invites -> {
            for (var invite : invites) {
                if (invite.getCode().equals(code)) {
                    String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    if (InviteData.addInvite(messageContext.getGuild(), invite.getCode(), name,
                            invite.getUses(), messageContext)) {
                        MessageSender.sendMessage("Added Invite **\"" + name + "\"** with code **"
                                        + invite.getCode() + "** to database with usage count of **"
                                        + invite.getUses() + "**",
                                messageContext.getChannel());
                    }
                    return;
                }
            }
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, messageContext.getChannel());
        });
    }
}
