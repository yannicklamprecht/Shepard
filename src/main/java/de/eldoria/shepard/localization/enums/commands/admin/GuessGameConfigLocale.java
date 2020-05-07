package de.eldoria.shepard.localization.enums.commands.admin;

public enum GuessGameConfigLocale {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.guessGameConfig.description"),
    /**
     * Localization key for subcommand addImage.
     */
    C_ADD_IMAGE("command.guessGameConfig.subcommand.addImage"),
    /**
     * Localization key for subcommand removeImage.
     */
    C_REMOVE_IMAGE("command.guessGameConfig.subcommand.removeImage"),
    /**
     * Localization key for subcommand changeFlag.
     */
    C_CHANGE_FLAG("command.guessGameConfig.subcommand.changeFlag"),
    /**
     * Localization key for subcommand showImageSet.
     */
    C_SHOW_IMAGE_SET("command.guessGameConfig.subcommand.showImageSet"),
    /**
     * Localization key for subcommand cancelRegistration.
     */
    C_CANCEL_REGISTRATION("command.guessGameConfig.subcommand.cancelRegistration"),
    /**
     * Localization key for argument flag.
     */
    A_FLAG("command.guessGameConfig.argument.flag"),
    AD_FLAG("command.guessGameConfig.argumentDescription.flag"),
    /**
     * Localization key for argument url.
     */
    A_URL("command.guessGameConfig.argument.url"),
    AD_URL("command.guessGameConfig.argumentDescription.url"),
    /**
     * Localization key for message.
     */
    M_REGISTRATION_CANCELED("command.guessGameConfig.messages.registrationCanceled"),
    /**
     * Localization key for message display image.
     */
    M_DISPLAY_IMAGE("command.guessGameConfig.messages.displayImage"),
    /**
     * Localization key for message changed flag.
     */
    M_CHANGED_FLAG("command.guessGameConfig.messages.changedFlag"),
    /**
     * Localization key for message removed image.
     */
    M_REMOVED_IMAGE("command.guessGameConfig.messages.removedImage"),
    /**
     * Localization key for message started registration.
     */
    M_STARTED_REGISTRATION("command.guessGameConfig.messages.startedRegistration");


    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    GuessGameConfigLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
