package de.eldoria.shepard.basemodules.commanddispatching;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class CommandDispatchingModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new CommandListener(), resources);
    }
}
