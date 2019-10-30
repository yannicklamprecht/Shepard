package de.eldoria.shepard.localization.enums.scheduler;

public enum ReminderLocale {
    M_REMINDER("reminderScheduler.message.reminder"),
    M_REMINDER_DIRECT_MESSAGE("reminderScheduler.message.reminderDirectMessage");

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
