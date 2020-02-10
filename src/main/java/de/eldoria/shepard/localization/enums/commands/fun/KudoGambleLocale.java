package de.eldoria.shepard.localization.enums.commands.fun;

public enum KudoGambleLocale {
    DESCRIPTION("command.kudogamble.description"),
    M_START("command.kudogamble.message.start"),
    M_GAMBLE("command.kudogamble.message.gamble"),
    M_WIN("command.kudogamble.message.win"),
    M_LOSE("command.kudogamble.message.lose"),
    M_PART_LOSE("command.kudogamble.message.partLose"),
    M_JACKPOT("command.kudogamble.message.jackpot");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    KudoGambleLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }


}
