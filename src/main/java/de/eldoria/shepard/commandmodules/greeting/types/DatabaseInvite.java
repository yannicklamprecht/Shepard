package de.eldoria.shepard.commandmodules.greeting.types;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;

@Getter
public class DatabaseInvite {
    private final String code;
    private final int usedCount;
    private final String source;
    private final Role role;

    /**
     * Creates a new database invite object.
     *
     * @param code      Code of the invite.
     * @param usedCount Count of usage of invite
     * @param source    source or name of the invite
     * @param role      role which should be assigned for this invite if present.
     */
    public DatabaseInvite(String code, int usedCount, String source, Role role) {
        this.code = code;
        this.usedCount = usedCount;
        this.source = source;
        this.role = role;
    }

    /**
     * Get the code of the invite.
     *
     * @return Code as string.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the count how often the invite was used.
     *
     * @return count as integer
     */
    public int getUses() {
        return usedCount;
    }

    /**
     * Get the source or name of the invite.
     *
     * @return Source or name as string
     */
    public String getSource() {
        return source;
    }
}
