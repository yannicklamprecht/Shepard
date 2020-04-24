package de.eldoria.shepard.webapi.apiobjects.commandserialization;

import lombok.Data;

import java.util.List;

/**
 * A class to serialise a list of {@link FullCommandInfo} to a json object.
 */
@Data
public class CommandInfos {
    private final List<FullCommandInfo> infos;

    /**
     * Creates a new CommandInfos object.
     *
     * @param infos list of commandInfo objects
     */
    public CommandInfos(List<FullCommandInfo> infos) {
        this.infos = infos;
    }
}
