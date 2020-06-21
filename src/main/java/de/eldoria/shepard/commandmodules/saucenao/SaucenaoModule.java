package de.eldoria.shepard.commandmodules.saucenao;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.saucenao.command.Saucenao;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class SaucenaoModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Saucenao(), resources);
    }
}
