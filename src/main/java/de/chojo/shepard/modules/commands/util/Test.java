package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.database.DatabaseConnector;
import de.chojo.shepard.messageHandler.Messages;
import de.chojo.shepard.database.DatabaseInvite;
import de.chojo.shepard.util.ListType;
import de.chojo.shepard.modules.commands.Command;
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
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        return true;
    }
}
