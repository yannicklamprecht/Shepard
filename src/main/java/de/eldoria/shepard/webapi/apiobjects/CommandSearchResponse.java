package de.eldoria.shepard.webapi.apiobjects;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.webapi.apiobjects.commandserialization.SimpleCommandInfo;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommandSearchResponse {
    private SimpleCommandInfo directMatch = null;
    private List<SimpleCommandInfo> fuzzyMatches;

    /**
     * Creates a new command search response.
     *
     * @param directMatch   command for return. found by direct match. can be null
     * @param fuzzySearched list of commands found by fuzzy search
     */
    public CommandSearchResponse(@Nullable Command directMatch, List<Command> fuzzySearched) {
        if (directMatch != null) {
            this.directMatch = new SimpleCommandInfo(directMatch);
            fuzzySearched.remove(directMatch);
        }
        fuzzyMatches = fuzzySearched.subList(0, Math.min(fuzzySearched.size(), 6)).stream()
                .map(SimpleCommandInfo::new).collect(Collectors.toList());
    }

}
