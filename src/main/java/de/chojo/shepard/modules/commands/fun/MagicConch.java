package de.chojo.shepard.modules.commands.fun;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.util.ArrayUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.ArrayList;
import java.util.Random;


public class MagicConch extends Command {
    private static final String[] positive = new String[]{"Yes", "Of Course", "DO IT!", "Why not :)"};
    private static final String[] negative = new String[]{"Are you dumb?", "Fly, you fool!", "nope", "No"};
    private static final String[] neutral = new String[]{"How should I know?", "Maybe", "You should ask someone else instead."};

    public MagicConch() {
        super("MagicConch", ArrayUtil.array("MagischeMiesmuschel"), "Find your decision!");
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        String word = "";
        Random rand = new Random();
        int type = rand.nextInt(3);

        if (type == 0)
            word = positive[rand.nextInt(positive.length)];
        else if (type == 1)
            word = negative[rand.nextInt(negative.length)];
        else if (type == 2)
            word = neutral[rand.nextInt(neutral.length)];


        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("The magic conch says:", word, false));

        Messages.sendTextBox(null, fields, receivedEvent.getChannel());

        return true;
    }
}
