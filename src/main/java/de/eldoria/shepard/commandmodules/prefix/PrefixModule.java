package de.eldoria.shepard.commandmodules.prefix;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class PrefixModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Prefix(), resources);
    }
}
