package de.eldoria.shepard.minigames;

public abstract class EvaluatorImpl implements Runnable {
    /**
     * Message id of evaluation message.
     */
    protected final long messageId;
    /**
     * Channel id for evaluation.
     */
    protected final long channelId;

    public EvaluatorImpl(long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }
}
