package de.eldoria.shepard.webapi.apiobjects.commandserialization;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.Data;

@Data
public class SimpleCommandInfo {
    private final String commandIdentifier;
    private final String commandName;
    private final String description;

    /**
     * Create a new simple command info object.
     *
     * @param command command for information retrieval
     */
    public SimpleCommandInfo(Command command) {
        commandIdentifier = command.getCommandIdentifier();
        commandName = command.getCommandName();
        description = TextLocalizer.localizeAllAndReplace(command.getCommandDesc(), EventWrapper.fakeEmpty());
    }
}
