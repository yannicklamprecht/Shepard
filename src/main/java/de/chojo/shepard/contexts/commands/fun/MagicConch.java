package de.chojo.shepard.contexts.commands.fun;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MagicConch extends Command {

    private final String[] positive = new String[] {"Yes", "Of Course", "DO IT!", "Why not :)"};
    private final String[] neutral = new String[] {"How should I know?", "Maybe",
            "You should ask someone else instead."};
    private final String[] negative = new String[] {"Are you dumb?", "Fly, you fool!", "nope", "No"};

    /**
     * Creates a new MagicConch command object.
     */
    public MagicConch() {
        commandName = "MagicConch";
        commandAliases = new String[] {"MagischeMiesmuschel"};
        commandDesc = "Find your decision!";
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String word = "";
        Random rand = new Random();
        int type = rand.nextInt(3);

        switch (type) {
            case 0:
                word = positive[rand.nextInt(positive.length)];
                break;
            case 1:
                word = negative[rand.nextInt(negative.length)];
                break;
            case 2:
                word = neutral[rand.nextInt(neutral.length)];
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }


        List<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("The magic conch says:", word, false));

        MessageSender.sendTextBox(null, fields, receivedEvent.getChannel());

        return true;
    }
}
