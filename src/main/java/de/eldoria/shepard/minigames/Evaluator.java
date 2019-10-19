package de.eldoria.shepard.minigames;

public abstract class Evaluator implements Runnable {
    protected long messageId;
    protected long channelId;

    public Evaluator(long messageId, long channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }
}
