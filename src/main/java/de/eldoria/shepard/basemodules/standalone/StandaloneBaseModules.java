package de.eldoria.shepard.basemodules.standalone;

import de.eldoria.shepard.basemodules.standalone.listener.LogListener;
import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class StandaloneBaseModules implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new LogListener(), resources);
    }
}
