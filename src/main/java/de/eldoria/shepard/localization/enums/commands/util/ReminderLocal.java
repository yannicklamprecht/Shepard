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
    C_DELETE("command.reminder.subcommand.remove"),
    C_SNOOZE("command.reminder.subcommand.snooze"),
    C_RESTORE("command.reminder.subcommand.restore"),
    /**
     * Localization key for subcommand list.
     */
    C_LIST("command.reminder.subcommand.list"),
    /**
     * Localization key for message removed.
     */
    M_REMOVED("command.reminder.message.removed"),
    M_TITLE_REMOVED("command.reminder.message.titleRemoved"),
    /**
     * Localization key for message format.
     */
    A_TIMESTAMP("command.reminder.argument.timestamp"),
    AD_TIMESTAMP("command.reminder.argumentDescription.timestamp"),
    /**
     * Localization key for message current reminders.
     */
    M_CURRENT_REMINDERS("command.reminder.message.currentReminder"),
    /**
     * Localization key for message remind date.
     */
    M_TITLE_CREATED("command.reminder.message.titleCreated"),
    M_TITLE_SNOOZED("command.reminder.message.titleSnoozed"),
    M_TITLE_RESTORED("command.reminder.message.titleRestored"),
    M_REMIND_DATE("command.reminder.message.remindDate"),
    M_REMIND_DISPLAY("command.reminder.message.remindDisplay"),
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
