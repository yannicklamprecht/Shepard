package de.eldoria.shepard.util;

public enum Emoji {
    CHECK_MARK_BUTTON("\u2705"),
    QUESTION_MARK("\u2753"),
    CROSS_MARK("\u274C");

    /**
     * Returns the unicode of the emoji.
     */
    public final String unicode;

    Emoji(String unicode) {
        this.unicode = unicode;
    }
}
