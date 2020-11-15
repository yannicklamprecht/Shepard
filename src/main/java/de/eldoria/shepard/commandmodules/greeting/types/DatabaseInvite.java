package de.eldoria.shepard.commandmodules.greeting.types;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

@Getter
public class DatabaseInvite {
    private final String code;
    private final int usedCount;
    @Nullable
    private final String source;
    @Nullable
    private final Role role;
    @Nullable
    private final User refer;

    /**
     * Creates a new database invite object.
     *
     * @param code      Code of the invite.
     * @param usedCount Count of usage of invite
     * @param source    source or name of the invite
     * @param role      role which should be assigned for this invite if present.
     */
    public DatabaseInvite(String code, int usedCount, String source, Role role, User refer) {
        this.code = code;
        this.usedCount = usedCount;
        this.source = source;
        this.role = role;
        this.refer = refer;
    }
}
