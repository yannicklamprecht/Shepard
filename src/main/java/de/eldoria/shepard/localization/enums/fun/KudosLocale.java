package de.eldoria.shepard.localization.enums.fun;

public enum KudosLocale {
    DESCRIPTION("command.kudos.description"),
    C_EMPTY("command.kudos.subcommand.empty"),
    C_GIVE("command.kudos.subcommand.give"),
    C_TOP("command.kudos.subcommand.top"),
    C_TOP_GLOBAL("command.kudos.subcommand.topGlobal"),
    A_POINTS("command.kudos.argument.points"),
    M_GLOBAL_RANKING("command.kudos.message.globalRanking"),
    M_SERVER_RANKING("command.kudos.message.serverRanking"),
    M_DESCRIPTION_GENERAL("command.kudos.message.embed.descriptionGeneral"),
    M_DESCRIPTION_EXTENDED("command.kudos.message.embed.descriptionExtended"),
    M_RECEIVED_KUDOS("command.kudos.message.receivedKudos");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    KudosLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
