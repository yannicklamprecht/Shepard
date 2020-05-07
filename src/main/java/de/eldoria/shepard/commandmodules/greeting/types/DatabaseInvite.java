package de.eldoria.shepard.commandmodules.greeting.types;

public class DatabaseInvite {
    private final String code;
    private final int usedCount;
    private final String source;

    /**
     * Creates a new database invite object.
     *
     * @param code      Code of the invite.
     * @param usedCount Count of usage of invite
     * @param source    source or name of the invite
     */
    public DatabaseInvite(String code, int usedCount, String source) {
        this.code = code;
        this.usedCount = usedCount;
        this.source = source;
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
    public int getUsedCount() {
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
