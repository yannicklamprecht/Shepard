package de.eldoria.shepard.localization.enums.commands.fun;

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
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    KudoLotteryLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
