package de.eldoria.shepard.localization.enums.botconfig;

public enum ManageContextLocale {
    DESCRIPTION("command.manageContext.description"),
    C_NSFW("command.manageContext.subcommand.setNsfw"),
    C_ADMIN("command.manageContext.subcommand.setAdminOnly"),
    M_ACTIVATED_ADMIN("command.manageContext.message.activatedAdmin"),
    M_DEACTIVATED_ADMIN("command.manageContext.message.deactivatedAdmin"),
    M_ACTIVATED_NSFW("command.manageContext.message.activatedNsfw"),
    M_DEACTIVATED_NSFW("command.manageContext.message.deactivatedNsfw");


    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    ManageContextLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

    }
