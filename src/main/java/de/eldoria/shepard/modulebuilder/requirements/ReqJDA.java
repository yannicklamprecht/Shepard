package de.eldoria.shepard.modulebuilder.requirements;

import net.dv8tion.jda.api.JDA;

public interface ReqJDA {
    /**
     * Add a {@link JDA} instance to the object.
     * @param jda jda instance
     */
    void addJDA(JDA jda);
}
