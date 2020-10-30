package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ReactionModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(resources, new Hug(), new Kiss(), new Slap(), new Spank(), new Lick(), new Blush(), new Cry());
    }
}
