package de.eldoria.shepard.localization.enums.admin;

public enum GuessGameConfigLocale {
    DESCRIPTION("command.guessGameConfig.description"),
    C_ADD_IMAGE("command.guessGameConfig.subcommand.addImage"),
    C_REMOVE_IMAGE("command.guessGameConfig.subcommand.removeImage"),
    C_CHANGE_FLAG("command.guessGameConfig.subcommand.changeFlag"),
    C_SHOW_IMAGE_SET("command.guessGameConfig.subcommand.showImageSet"),
    C_CANCEL_REGISTRATION("command.guessGameConfig.subcommand.cancelRegistration"),
    A_FLAG("command.guessGameConfig.argument.flag"),
    A_URL("command.guessGameConfig.argument.url"),
    M_REGISTRATION_CANCELED("command.guessGameConfig.messages.registrationCanceled"),
    M_DISPLAY_IMAGE("command.guessGameConfig.messages.displayImage"),
    M_CHANGED_FLAG("command.guessGameConfig.messages.changedFlag"),
    M_REMOVED_IMAGE("command.guessGameConfig.messages.removedImage"),
    M_STARTED_REGISTRATION("command.guessGameConfig.messages.startedRegistration");


    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String replacement;

    GuessGameConfigLocale(String localeCode) {
        this.localeCode = localeCode;
        this.replacement = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return replacement;
    }

}
