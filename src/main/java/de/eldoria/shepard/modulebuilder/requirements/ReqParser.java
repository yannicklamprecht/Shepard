package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;

public interface ReqParser {
    /**
     * Add a {@link ArgumentParser} to the object.
     * @param parser argument parser instance
     */
    void addParser(ArgumentParser parser);
}
