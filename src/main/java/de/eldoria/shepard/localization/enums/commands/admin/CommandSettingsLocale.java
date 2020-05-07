package de.eldoria.shepard.localization.enums.commands.admin;

public enum CommandSettingsLocale {
    DESCRIPTION("command.commandSettings.description"),
    C_ENABLE_COMMAND("command.commandSettings.subcommand.enableCommand"),
    C_DISABLE_COMMAND("command.commandSettings.subcommand.disableCommand"),
    C_STATE_LIST("command.commandSettings.subcommand.stateList"),
    C_ENABLE_CHANNEL_CHECK("command.commandSettings.subcommand.enableChannelCheck"),
    C_DISABLE_CHANNEL_CHECK("command.commandSettings.subcommand.disableChannelCheck"),
    C_SET_LIST_TYPE("command.commandSettings.subcommand.setListType"),
    C_ADD_CHANNEL("command.commandSettings.subcommand.addChannel"),
    C_REMOVE_CHANNEL("command.commandSettings.subcommand.removeChannel"),
    C_CHANNEL_LIST("command.commandSettings.subcommand.channelList"),
    M_ADD_CHANNEL("command.commandSettings.message.addChannel"),
    M_REMOVED_CHANNEL("command.commandSettings.message.removedChannel"),
    M_CAN_BE_USED_NOW("command.commandSettings.message.canBeUsedNow"),
    M_CAN_NOT_BE_USED_NOW("command.commandSettings.message.canNotBeUsedNow"),
    M_CHECK_NOT_ACTIVE("command.commandSettings.message.checkNotActive"),
    M_ENABLED_COMMAND("command.commandSettings.message.enabledCommand"),
    M_DISABLED_COMMAND("command.commandSettings.message.disabledCommand"),
    M_CAN_NOT_DISABLE_COMMAND("command.commandSettings.message.canNotDisableCommand"),
    M_CHANNEL_SETTINGS("command.commandSettings.message.commandSettings"),
    M_CAN_BE_USED_IN_CHANNEL("command.commandSettings.message.canBeUsedInChannel"),
    M_CAN_NOT_BE_USED_IN_CHANNEL("command.commandSettings.message.canNotBeUsedInChannel"),
    M_CAN_BE_USED_EVERYWHERE("command.commandSettings.message.canBeUsedEverywhere"),
    M_BLACKLIST("command.commandSettings.message.blacklist"),
    M_WHITELIST("command.commandSettings.message.whitelist"),
    M_ENABLED_CHANNEL_CHECK("command.commandSettings.message.enabledChannelCheck"),
    M_DISABLED_CHANNEL_CHECK("command.commandSettings.message.disabledChannelCheck"),
    M_CAN_NOT_ENABLE_CHANNEL_CHECK("command.commandSettings.message.canNotEnableChannelCheck");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    CommandSettingsLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
