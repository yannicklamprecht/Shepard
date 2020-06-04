package de.eldoria.shepard.commandmodules.modlog;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.modlog.commands.ModLog;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ModLogModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new ModLog(), resources);
    }
}
