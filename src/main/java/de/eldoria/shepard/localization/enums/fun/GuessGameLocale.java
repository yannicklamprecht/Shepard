package de.eldoria.shepard.localization.enums.fun;

public enum GuessGameLocale {
    DESCRIPTION("command.guessGame.description"),
    C_START("command.guessGame.subcommand.startGame"),
    C_SCORE("command.guessGame.subcommand.score"),
    C_SCORE_GLOBAL("command.guessGame.subcommand.globalScore"),
    C_TOP("command.guessGame.subcommand.top"),
    C_TOP_GLOBAL("command.guessGame.subcommand.topGlobal"),
    M_MINIGAME_CHANNEL("command.guessGame.message.minigameChannel"),
    M_SCORE("command.guessGame.message.score"),
    M_SCORE_GLOBAL("command.guessGame.message.scoreGlobal"),
    M_SERVER_RANKING("command.guessGame.message.serverRanking"),
    M_GLOBAL_RANKING("command.guessGame.message.globalRanking"),
    M_ROUND_IN_PROGRESS("command.guessGame.message.roundInProgress"),
    M_TITLE("command.guessGame.message.embed.title"),
    M_GAME_DESCRIPTION("command.guessGame.message.embed.gameDescription"),
    M_GAME_FOOTER("command.guessGame.message.embed.gameFooter");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    GuessGameLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
