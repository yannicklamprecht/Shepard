package de.eldoria.shepard.basemodules.commanddispatching.dialogue;

import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;

public interface Dialog {
    boolean invoke(EventWrapper wrapper, Message message);
}
