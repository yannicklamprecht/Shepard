package de.eldoria.shepard.localization.enums.commands.fun;

public enum GuessGameLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.guessGame.description"),
    /**
     * Localization key for subcommand start.
     */
    C_START("command.guessGame.subcommand.startGame"),
    /**
     * Localization key for subcommand score.
     */
    C_SCORE("command.guessGame.subcommand.score"),
    /**
     * Localization key for subcommand scoreGlobal.
     */
    C_SCORE_GLOBAL("command.guessGame.subcommand.globalScore"),
    /**
     * Localization key for subcommand top.
     */
    C_TOP("command.guessGame.subcommand.top"),
    /**
     * Localization key for subcommand topGlobal.
     */
    C_TOP_GLOBAL("command.guessGame.subcommand.topGlobal"),
    /**
     * Localization key for message minigame channel.
     */
    M_MINIGAME_CHANNEL("command.guessGame.message.minigameChannel"),
    /**
     * Localization key for message score.
     */
    M_SCORE("command.guessGame.message.score"),
    /**
     * Localization key for message score global.
     */
    M_SCORE_GLOBAL("command.guessGame.message.scoreGlobal"),
    /**
     * Localization key for message server ranking.
     */
    M_SERVER_RANKING("command.guessGame.message.serverRanking"),
    /**
     * Localization key for message global ranking.
     */
    M_GLOBAL_RANKING("command.guessGame.message.globalRanking"),
    /**
     * Localization key for message round in progress.
     */
    M_ROUND_IN_PROGRESS("command.guessGame.message.roundInProgress"),
    /**
     * Localization key for message title.
     */
    M_TITLE("command.guessGame.message.embed.title"),
    /**
     * Localization key for message game description.
     */
    M_GAME_DESCRIPTION("command.guessGame.message.embed.gameDescription"),
    /**
     * Localization key for message game footer.
     */
    M_GAME_FOOTER("command.guessGame.message.embed.gameFooter");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    GuessGameLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
