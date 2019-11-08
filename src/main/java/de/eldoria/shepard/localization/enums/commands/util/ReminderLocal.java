package de.eldoria.shepard.localization.enums.commands.util;

public enum ReminderLocal {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.reminder.description"),
    /**
     * Localization key for subcommand add.
     */
    C_ADD("command.reminder.subcommand.add"),
    /**
     * Localization key for subcommand remove.
     */
    C_REMOVE("command.reminder.subcommand.remove"),
    /**
     * Localization key for subcommand list.
     */
    C_LIST("command.reminder.subcommand.list"),
    /**
     * Localization key for message removed.
     */
    M_REMOVED("command.reminder.message.removed"),
    /**
     * Localization key for message format.
     */
    M_FORMAT("command.reminder.message.addFormat"),
    /**
     * Localization key for message current reminders.
     */
    M_CURRENT_REMINDERS("command.reminder.message.currentReminder"),
    /**
     * Localization key for message remind date.
     */
    M_REMIND_DATE("command.reminder.message.remindDate"),
    /**
     * Localization key for message remind time.
     */
    M_REMIND_TIME("command.reminder.message.remindTime");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ReminderLocal(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
