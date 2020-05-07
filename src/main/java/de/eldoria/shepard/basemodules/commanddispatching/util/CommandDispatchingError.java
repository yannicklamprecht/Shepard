package de.eldoria.shepard.basemodules.commanddispatching.util;

public class CommandDispatchingError extends RuntimeException {
    /**
     * Create a new command dispatching error.
     *
     * @param message message to send.
     */
    public CommandDispatchingError(String message) {
        super(message);
    }

    /**
     * Create a new command dispatching error.
     */
    public CommandDispatchingError() {
    }
}
