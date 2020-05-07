package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.commandmodules.privatemessages.PrivateMessageCollection;

public interface ReqPrivateMessages {
    /**
     * Add a {@link PrivateMessageCollection} to the object.
     *
     * @param privateMessages private message collection instance
     */
    void addPrivateMessages(PrivateMessageCollection privateMessages);
}
