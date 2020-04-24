package de.eldoria.shepard.webapi.apiobjects.commandserialization;

import lombok.Data;

import java.util.List;

@Data
public class SimpleCommandInfos {
    private final List<SimpleCommandInfo> infos;

    /**
     * Create a new simple command info collection.
     *
     * @param infos list of command info objects
     */
    public SimpleCommandInfos(List<SimpleCommandInfo> infos) {
        this.infos = infos;
    }
}
