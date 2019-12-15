package de.eldoria.shepard.webapi.apiobjects;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.util.TextLocalizer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSearchResponse {
    private CommandSearchInfo directMatch;
    private List<CommandSearchInfo> fuzzyMatches;

    /**
     * Creates a new command search response.
     * @param directMatch command for return. found by direct match. can be null
     * @param fuzzySearched list of commands found by fuzzy search
     */
    public CommandSearchResponse(@Nullable Command directMatch, List<Command> fuzzySearched) {
        if (directMatch != null) {
            this.directMatch = new CommandSearchInfo(directMatch);
            fuzzySearched.remove(directMatch);
        }
        fuzzyMatches = fuzzySearched.subList(0, Math.min(fuzzySearched.size(), 6)).stream()
                .map(CommandSearchInfo::new).collect(Collectors.toList());
    }

    private class CommandSearchInfo {
        private String contextName;
        private String commandName;
        private String description;

        CommandSearchInfo(Command command) {
            contextName = command.getContextName();
            commandName = command.getCommandName();
            description = TextLocalizer.localizeAllAndReplace(command.getCommandDesc(), null);
        }
    }
}
