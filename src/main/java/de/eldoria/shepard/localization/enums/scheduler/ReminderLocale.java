package de.eldoria.shepard.localization.enums.scheduler;

public enum ReminderLocale {
    /**
     * Localization key for message reminder.
     */
    M_REMINDER("reminderScheduler.message.reminder");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    /**
     * Create a new locale object.
     *
     * @param localeCode locale code
     */
    ReminderLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
