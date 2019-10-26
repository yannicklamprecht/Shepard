package de.eldoria.shepard.localization.enums.fun;

public enum KudoLotteryLocale {
    DESCRIPTION("command.kudoLottery.description"),
    M_LOTTERY_RUNNING("command.kudoLottery.message.lotteryRunning"),
    M_EMBED_TITLE("command.kudoLottery.message.embed.title"),
    M_EMBED_DESCRIPTION("command.kudoLottery.message.embed.description"),
    M_EMBED_KUDOS_IN_POT("command.kudoLottery.message.embed.kudosInPot"),
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
