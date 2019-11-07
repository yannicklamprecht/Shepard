package de.eldoria.shepard.minigames;

public abstract class BaseEvaluator implements Runnable {
    /**
     * Message id of evaluation message.
     */
    protected final long messageId;
    /**
     * Channel id for evaluation.
     */
    protected final long channelId;

    /**
     * Creates a new base evaluator.
     * @param messageId message for evaluation
     * @param channelId channel where the message was send
     */
    protected BaseEvaluator(long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }
}
