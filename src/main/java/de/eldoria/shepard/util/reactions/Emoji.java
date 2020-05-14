package de.eldoria.shepard.util.reactions;

@SuppressWarnings("CheckStyle")
public enum Emoji {
    /**
     * :white_check_mark: emoji.
     */
    CHECK_MARK_BUTTON("✅"),
    /**
     * :x: emoji.
     */
    CROSS_MARK("❌"),
    /**
     * :question: emoji.
     */
    QUESTION_MARK("❓"),

    /**
     * :moneybag: emoji.
     */
    MONEY_BAG("💰"),

    /**
     * :dollar: emoji.
     */
    DOLLAR("💵"),

    /**
     * :gem: emoji.
     */
    GEM("💎"),

    /**
     * :black_large_square: emoji.
     */
    BLACK_LARGE_SQUARE("⬛"),
    /**
     * :tada: emoji.
     */
    TADA("🎉"),
    /**
     * :diamond_shape_with_a_dot_inside: emoji.
     */
    DIAMAOND_SHAPE_WITH_DOT("💠"),
    ARROWS_COUNTERBLOCKWISE("🔄");

    /**
     * Returns the unicode of the emoji.
     */
    public final String unicode;

    /**
     * Create a new emoji.
     *
     * @param unicode unicode of emoji.
     */
    Emoji(String unicode) {
        this.unicode = unicode;
    }
}
