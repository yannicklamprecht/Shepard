package de.eldoria.shepard.util;

public enum ExitCode {
    SHUTDOWN(0),
    RESTART(10);

    /**
     * Code for exit.
     */
    public final int code;

    /**
     * Creates a new exit code with a integer identifier.
     *
     * @param code identifier
     */
    ExitCode(int code) {
        this.code = code;
    }
}
