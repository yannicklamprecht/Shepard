package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.database.DatabaseQuery;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import de.chojo.shepard.contexts.commands.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RegisterInviteLink extends Command {
// TODO: Implement new Database functions

    public RegisterInviteLink() {
        commandName = "registerInviteLink";
        commandDesc = "Registers an invite link for the server";
        arguments = new CommandArg[]{new CommandArg("code", "Code of Invitlink", true),
                new CommandArg("name", "name of the invitelink", true)};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        Invite invite = receivedEvent.getGuild().retrieveInvites().complete()
                .stream()
                .filter(inv -> inv.getCode().equals(args[1]))
                .findAny()
                .orElseThrow(() -> new CommandException("No invite found"));
        DatabaseQuery.saveInvite(invite, args[2],  receivedEvent.getGuild().getId());
        return false;
    }
}
