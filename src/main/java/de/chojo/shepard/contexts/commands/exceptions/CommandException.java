package de.chojo.shepard.contexts.commands.exceptions;

public class CommandException extends IllegalArgumentException {

    /**
     * Creates a new command exception.
     *
     * @param message Message of the exception
     */
    public CommandException(String message) {
        super(message);
    }
}
