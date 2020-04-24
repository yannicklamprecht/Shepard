package de.eldoria.shepard.commandmodules.reminder;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.reminder.scheduler.ReminderScheduler;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ReminderModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Reminder(), resources);
        addAndInit(new ReminderScheduler(), resources);
    }
}
