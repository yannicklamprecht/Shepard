package de.eldoria.shepard.localization.enums.fun;

public enum KudoLotteryLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.kudoLottery.description"),
    /**
     * Localization key for message lottery running.
     */
    M_LOTTERY_RUNNING("command.kudoLottery.message.lotteryRunning"),
    /**
     * Localization key for message embed title.
     */
    M_EMBED_TITLE("command.kudoLottery.message.embed.title"),
    /**
     * Localization key for message embed description.
     */
    M_EMBED_DESCRIPTION("command.kudoLottery.message.embed.description"),
    /**
     * Localization key for message kembed kudos in pot.
     */
    M_EMBED_KUDOS_IN_POT("command.kudoLottery.message.embed.kudosInPot"),
    /**
     * Localization key for message embed explanation.
     */
    M_EMBED_EXPLANATION("command.kudoLottery.message.embed.explanation");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    KudoLotteryLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }
}
