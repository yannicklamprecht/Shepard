package de.eldoria.shepard.localization.enums.commands.fun;

public enum ReactionLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION_HUG("command.reaction.description.hug"),
    DESCRIPTION_KISS("command.reaction.description.kiss"),
    DESCRIPTION_SLAP("command.reaction.description.slap"),
    DESCRIPTION_SPANK("command.reaction.description.spank"),
    DESCRIPTION_CRY("command.reaction.description.cry"),
    DESCRIPTION_BLUSH("command.reaction.description.blush"),
    DESCRIPTION_LICK("command.reaction.description.lick"),
    C_HUG("command.reaction.command.hug"),
    C_HUG_OTHER("command.reaction.command.otherHug"),
    C_KISS("command.reaction.command.kiss"),
    C_KISS_OTHER("command.reaction.command.otherKiss"),
    C_SLAP("command.reaction.command.slap"),
    C_SLAP_OTHER("command.reaction.command.otherSlap"),
    C_SPANK("command.reaction.command.spank"),
    C_SPANK_OTHER("command.reaction.command.otherSpank"),
    C_CRY("command.reaction.command.cry"),
    C_CRY_OTHER("command.reaction.command.otherCry"),
    C_BLUSH("command.reaction.command.blush"),
    C_BLUSH_OTHER("command.reaction.command.otherBlush"),
    C_LICK("command.reaction.command.lick"),
    C_LICK_OTHER("command.reaction.command.otherLick"),
    M_HUG("command.reaction.message.hug"),
    M_HUG_SELF("command.reaction.message.selfHug"),
    M_KISS("command.reaction.message.kiss"),
    M_KISS_SELF("command.reaction.message.selfKiss"),
    M_SLAP("command.reaction.message.slap"),
    M_SLAP_SELF("command.reaction.message.selfSlap"),
    M_SPANK("command.reaction.message.spank"),
    M_SPANK_SELF("command.reaction.message.selfSpank"),
    M_CRY("command.reaction.message.cry"),
    M_CRY_SELF("command.reaction.message.selfCry"),
    M_BLUSH("command.reaction.message.blush"),
    M_BLUSH_SELF("command.reaction.message.selfBlush"),
    M_LICK("command.reaction.message.lick"),
    M_LICK_SELF("command.reaction.message.selfLick"),

    /**
     * Localization key for argument say message.
     */
    OTHER("command.cute.outputOther"),
    C_EMPTY("command.cute.empty"),
    C_OTHER("command.cute.other")
    ;

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ReactionLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
