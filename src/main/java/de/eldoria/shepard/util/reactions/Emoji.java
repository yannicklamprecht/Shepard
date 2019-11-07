package de.eldoria.shepard.util.reactions;

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
    QUESTION_MARK("\u2753"),

    /**
     * :moneybag: emoji.
     */
    MONEY_BAG("\ud83d\udcb0"),

    /**
     * :dollar: emoji.
     */
    DOLLAR("\ud83d\udcb5"),

    /**
     * :gem: emoji.
     */
    GEM("\uD83D\uDC8E");

    /**
     * Returns the unicode of the emoji.
     */
    public final String unicode;

    /**
     * Create a new emoji.
     * @param unicode unicode of emoji.
     */
    Emoji(String unicode) {
        this.unicode = unicode;
    }
}
