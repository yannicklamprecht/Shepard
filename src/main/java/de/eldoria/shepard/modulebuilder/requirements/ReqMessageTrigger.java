package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.wrapper.EventWrapper;

public interface ReqMessageTrigger {
    /**
     * Is triggered when the bot receives a message on a private or guild channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onMessageReceived(EventWrapper wrapper) {

    }

    /**
     * Is triggered when the bot receives a message on a private channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onPrivateMessageReceived(EventWrapper wrapper) {

    }

    /**
     * Is triggered when the bot receives a message on a guild channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onGuildMessageReceived(EventWrapper wrapper) {

    }

    /**
     * Is triggered when the bot receives a message on a private or guild channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onMessageUpdate(EventWrapper wrapper) {

    }

    /**
     * Is triggered when the bot receives a message on a private channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onPrivateMessageUpdate(EventWrapper wrapper) {

    }

    /**
     * Is triggered when the bot receives a message on a guild channel.
     *
     * @param wrapper wrapper which contains the event data.
     */
    default void onGuildMessageUpdate(EventWrapper wrapper) {

    }
}
