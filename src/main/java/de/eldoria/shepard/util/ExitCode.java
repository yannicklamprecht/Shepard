package de.eldoria.shepard.util;

public enum ExitCode {
    SHUTDOWN(0),
    RESTART(10);

    public final int code;

    ExitCode(int code) {
        this.code = code;
    }
}
