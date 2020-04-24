package de.eldoria.shepard.core;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class CoreModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new SQLUpdater(), resources);
        addAndInit(new ConsoleReader(), resources);
        addAndInit(new LanguageHandler(), resources);
    }
}
