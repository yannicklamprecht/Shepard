package de.eldoria.shepard.commandmodules.badwords;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.badwords.command.BadWords;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class BadWordsModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new BadWords(), resources);
    }
}
