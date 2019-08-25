package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class Oha extends Command {

    /**
     * creates a new oha keyword object.
     */
    public Oha() {
        commandName = "oha";
        commandDesc = "Ohaaaaaa";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String oha = "oha";
        Random rand = new Random();
        int loops = rand.nextInt(30) + 10;
        oha = oha + "a".repeat(loops);
        MessageSender.sendMessage(oha, receivedEvent.getChannel());
    }
}
