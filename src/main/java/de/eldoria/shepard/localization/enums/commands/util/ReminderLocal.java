package de.eldoria.shepard.localization.enums.commands.util;

public enum ReminderLocal {
    /**
     * Localization key for description.
     */
    DESCRIPTION("command.reminder.description"),
    C_ADD("command.reminder.subcommand.add"),
    C_REMOVE("command.reminder.subcommand.remove"),
    C_LIST("command.reminder.subcommand.list"),
    M_REMOVED("command.reminder.message.removed"),
    M_FORMAT("command.reminder.message.addFormat"),
    M_CURRENT_REMINDERS("command.reminder.message.currentReminder"),
    M_REMIND_DATE("command.reminder.message.remindDate"),
    M_REMIND_TIME("command.reminder.message.remindTime");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ReminderLocal(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }
}
