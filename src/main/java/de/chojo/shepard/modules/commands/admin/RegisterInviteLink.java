package de.chojo.shepard.modules.commands.admin;

import de.chojo.shepard.database.DatabaseQuery;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import de.chojo.shepard.util.ArrayUtil;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RegisterInviteLink extends Command {

    public RegisterInviteLink() {
        super("registerInviteLink", "Registers an invite link for the server",
                ArrayUtil.array(
                        new CommandArg("code", "Code of Invitlink", true),
                        new CommandArg("name", "name of the invitelink", true))
        );
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length != 3) {
            Messages.sendSimpleError("Missing arguments", receivedEvent.getChannel());
            return false;
        }
        Invite invite = receivedEvent.getGuild().getInvites().complete()
                .stream()
                .filter(inv -> inv.getCode().equals(args[1]))
                .findAny()
                .orElseThrow(() -> new CommandException("No invite found"));
        DatabaseQuery.saveInvite(invite, args[2],  receivedEvent.getGuild().getId());
        return false;
    }
}
