package de.eldoria.shepard.util;

public enum Emoji {
    CHECK_MARK_BUTTON("U+2705"),
    QUESTION_MARK("U+2753"),
    CROSS_MARK("U+274C");

    /**
     * Returns the unicode of the emoji.
     */
    public final String unicode;

    Emoji(String unicode) {
        this.unicode = unicode;
    }
}
