package de.eldoria.shepard.commandmodules.greeting;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.greeting.commands.Greeting;
import de.eldoria.shepard.commandmodules.greeting.commands.InviteDetection;
import de.eldoria.shepard.commandmodules.greeting.listener.GreetingListener;
import de.eldoria.shepard.commandmodules.greeting.routines.InviteScheduler;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class GreetingModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Greeting(), resources);
        addAndInit(new InviteDetection(), resources);
        addAndInit(new GreetingListener(), resources);
        addAndInit(new InviteScheduler(), resources);
    }
}
