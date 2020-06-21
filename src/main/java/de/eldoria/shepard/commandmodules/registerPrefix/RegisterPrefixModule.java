package de.eldoria.shepard.commandmodules.registerPrefix;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.registerPrefix.command.RegisterPrefix;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class RegisterPrefixModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new RegisterPrefix(), resources);
    }
}
