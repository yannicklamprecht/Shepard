package de.eldoria.shepard.basemodules.reactionactions;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class ReactionActionModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new ReactionActionListener(), resources);
    }
}
