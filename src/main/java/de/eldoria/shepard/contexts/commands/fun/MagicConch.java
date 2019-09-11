package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collections;
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
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        String word;
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

        MessageSender.sendTextBox(null,
                Collections.singletonList(new MessageEmbed.Field("The magic conch says:", word, false)),
                dataWrapper.getChannel());

    }
}
