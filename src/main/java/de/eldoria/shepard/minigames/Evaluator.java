package de.eldoria.shepard.minigames;

public abstract class Evaluator implements Runnable {
    /**
     * Message id of evaluation message.
     */
    protected final long messageId;
    /**
     * Channel id for evaluation.
     */
    protected final long channelId;

    public Evaluator(long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }
}
