package de.eldoria.shepard.localization.enums.scheduler;

public enum ReminderLocale {
    /**
     * Localization key for message reminder.
     */
    M_REMINDER("reminderScheduler.message.reminder");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    ReminderLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
