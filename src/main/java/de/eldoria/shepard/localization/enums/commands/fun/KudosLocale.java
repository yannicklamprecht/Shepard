package de.eldoria.shepard.localization.enums.commands.fun;

public enum KudosLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.kudos.description"),
    /**
     * Localization key for subcommand empty.
     */
    C_EMPTY("command.kudos.subcommand.empty"),
    /**
     * Localization key for subcommand give.
     */
    C_GIVE("command.kudos.subcommand.give"),
    /**
     * Localization key for subcommand top.
     */
    C_TOP("command.kudos.subcommand.top"),
    /**
     * Localization key for subcommand topGlobal.
     */
    C_TOP_GLOBAL("command.kudos.subcommand.topGlobal"),
    /**
     * Localization key for argument points.
     */
    A_POINTS("command.kudos.argument.points"),
    /**
     * Localization key for message global ranking.
     */
    M_GLOBAL_RANKING("command.kudos.message.globalRanking"),
    /**
     * Localization key for message server ranking.
     */
    M_SERVER_RANKING("command.kudos.message.serverRanking"),
    /**
     * Localization key for message description general.
     */
    M_DESCRIPTION_GENERAL("command.kudos.message.embed.descriptionGeneral"),
    /**
     * Localization key for message description extended.
     */
    M_DESCRIPTION_EXTENDED("command.kudos.message.embed.descriptionExtended"),
    /**
     * Localization key for message received kudos.
     */
    M_RECEIVED_KUDOS("command.kudos.message.receivedKudos");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     * @param localeCode locale code
     */
    KudosLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
