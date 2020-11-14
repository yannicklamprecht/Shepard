package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ReactionModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(resources, new Hug(), new Kiss(), new Slap(), new Spank(), new Lick(), new Blush(), new Cry(),
                new Headpat(), new Confused(), new Dance(), new Nom(), new Poke(), new Punish(), new Shrug(), new Sleep(),
                new Smug(), new Wave());
    }
}
