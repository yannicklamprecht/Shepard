package de.chojo.shepard.contexts.reactionmessages.reactionmessage;

import de.chojo.shepard.contexts.reactionmessages.ReactionMessage;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class Test extends ReactionMessage {
    public Test () {
        messageIds = new String[]{"539525087053873162"};
    }

    public boolean execute(MessageReactionAddEvent e) {
        e.getChannel().sendMessage("You reacted with " + e.getReactionEmote().getName()).queue();
        return true;
    }
}
