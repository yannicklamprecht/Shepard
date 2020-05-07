package de.eldoria.shepard.localization.enums.commands.botconfig;

public enum ManageContextLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.manageContext.description"),
    /**
     * Localization key for subcommand setNsfw.
     */
    C_NSFW("command.manageContext.subcommand.setNsfw"),
    /**
     * Localization key for subcommand setAdminOnly.
     */
    C_ADMIN("command.manageContext.subcommand.setAdminOnly"),
    /**
     * Localization key for subcommand setUserCooldown.
     */
    C_USER_COOLDOWN("command.manageContext.subcommand.setUserCooldown"),
    /**
     * Localization key for subcommand setGuildCooldown.
     */
    C_GUILD_COOLDOWN("command.manageContext.subcommand.setGuildCooldown"),
    /**
     * Localization key for message activate admin.
     */
    M_ACTIVATED_ADMIN("command.manageContext.message.activatedAdmin"),
    /**
     * Localization key for message deactivate admin.
     */
    M_DEACTIVATED_ADMIN("command.manageContext.message.deactivatedAdmin"),
    /**
     * Localization key for message activated nsfw.
     */
    M_ACTIVATED_NSFW("command.manageContext.message.activatedNsfw"),
    /**
     * Localization key for message deactivated nsfw.
     */
    M_DEACTIVATED_NSFW("command.manageContext.message.deactivatedNsfw"),
    /**
     * Localization key for message set user cooldown.
     */
    M_SET_USER_COOLDOWN("command.manageContext.message.setUserCooldown"),
    /**
     * Localization key for message set guild cooldown.
     */
    M_SET_GUILD_COOLDOWN("command.manageContext.message.setGuildCooldown");


    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ManageContextLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
