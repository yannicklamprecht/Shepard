package de.eldoria.shepard.basemodules.commanddispatching.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;

public class UnkownCommandDispachingMethod extends RuntimeException {

    /**
     * Create a new error when a command does not user the {@link Executable} or {@link ExecutableAsync} interface.
     *
     * @param command command which lacks of the interfaces.
     */
    public UnkownCommandDispachingMethod(Command command) {
        super("Unknown command dispatching method in command " + command.getCommandIdentifier());
    }
}
