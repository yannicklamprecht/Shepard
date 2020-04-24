package de.eldoria.shepard.commandmodules.changelog;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ChangelogModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Changelog(), resources);
        addAndInit(new ChangelogListener(), resources);
    }
}
