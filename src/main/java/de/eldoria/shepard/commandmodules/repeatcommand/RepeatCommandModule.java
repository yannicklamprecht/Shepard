package de.eldoria.shepard.commandmodules.repeatcommand;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class RepeatCommandModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new RepeatCommand(), resources);
    }
}
