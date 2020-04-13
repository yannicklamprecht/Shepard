package de.eldoria.shepard.collections;

import com.google.gson.GsonBuilder;
import de.eldoria.shepard.contexts.commands.CommandInfo;

import java.util.List;

/**
 * A class to serialise a list of {@link CommandInfo} to a json object.
 */
public class CommandInfos {
    private List<CommandInfo> infos;

    /**
     * Creates a new CommandInfos object.
     *
     * @param infos list of commandInfo objects
     */
    public CommandInfos(List<CommandInfo> infos) {
        this.infos = infos;
    }

    /**
     * Get the object as json string.
     *
     * @return object as json string
     */
    public String asJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
