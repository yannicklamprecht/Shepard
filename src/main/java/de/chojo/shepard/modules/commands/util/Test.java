package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.DatabaseConnector;
import de.chojo.shepard.Messages;
import de.chojo.shepard.util.DatabaseInvite;
import de.chojo.shepard.util.ListType;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Test extends Command {
    public Test() {
        commandName = "test";
        commandAliases = new String[]{};
        commandDesc = "Testcommand!";
        characterCheckEnabled = true;
        characterListType = ListType.Whitelist;
        characterList = new String[]{"214347948316819456", "442418965042429993"};
        args = null;
    }

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {

        for (DatabaseInvite inv : DatabaseConnector.getInvites(receivedEvent.getGuild().getId())) {
        Messages.sendMessage(inv.getName() + "", channel);
        }
        return true;
    }
}
