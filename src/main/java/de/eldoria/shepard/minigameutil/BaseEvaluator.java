package de.eldoria.shepard.minigameutil;

import net.dv8tion.jda.api.entities.Message;

import java.util.Optional;

public abstract class BaseEvaluator implements Runnable {
    /**
     * Message id of evaluation message.
     */
    protected long messageId;
    /**
     * Channel id for evaluation.
     */
    protected final long channelId;

    public BaseEvaluator(long channelId) {
        this.channelId = channelId;
    }

    /**
     * Creates a new base evaluator.
     *
     * @param messageId message for evaluation
     * @param channelId channel where the message was send
     */
    protected BaseEvaluator(long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }

    /**
     * Starts a evaluation process
     *
     * @return id of message
     */
    public abstract Optional<Message> start();
}
