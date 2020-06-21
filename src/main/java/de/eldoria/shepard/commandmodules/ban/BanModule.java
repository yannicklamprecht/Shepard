package de.eldoria.shepard.commandmodules.ban;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.ban.command.Ban;
import de.eldoria.shepard.commandmodules.ban.schedular.BanScheduler;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class BanModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new Ban(), resources);
        addAndInit(new BanScheduler(), resources);
    }
}
