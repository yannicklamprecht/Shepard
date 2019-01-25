package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.messageHandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class Oha extends Command {
    public Oha() {
        commandName = "oha";
        commandAliases = null;
        commandDesc = "Ohaaaaaa";
        args = null;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        String oha = "oha";
        Random rand = new Random();
        int loops = rand.nextInt(30) + 10;
        for (int i = 0; i < loops; i++){
            oha = oha.concat("a");
        }
        Messages.sendMessage(oha, receivedEvent.getChannel());
        return true;

    }
}
