package de.eldoria.shepard.commandmodules.presence;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class PresenceModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        PresenceChanger presenceChanger = new PresenceChanger();
        addAndInit(resources, presenceChanger, new BotPresence(presenceChanger));
    }
}
