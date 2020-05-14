package de.eldoria.shepard.localization.enums.scheduler;

public enum ReminderSchedulerLocale {
    /**
     * Localization key for message reminder.
     */
    M_REMINDER("reminderScheduler.message.reminder"),
    M_REMINDER_GUILD("reminderScheduler.message.reminderGuild"),
    M_REMINDER_SNOOZED("reminderScheduler.message.snoozed"),
    M_REACT_COMMAND("reminderScheduler.message.reactCommand"),
    M_COMMAND("reminderScheduler.message.command");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ReminderSchedulerLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
