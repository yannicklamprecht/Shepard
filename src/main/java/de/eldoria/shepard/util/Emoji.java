package de.eldoria.shepard.util;

public enum Emoji {
    /**
     * :white_check_mark: emoji.
     */
    CHECK_MARK_BUTTON("\u2705"),
    /**
     * :x: emoji.
     */
    CROSS_MARK("\u274c"),
    /**
     * :question: emoji.
     */
    QUESTION_MARK("\u2753");

    /**
     * Returns the unicode of the emoji.
     */
    public final String unicode;

    Emoji(String unicode) {
        this.unicode = unicode;
    }
}
