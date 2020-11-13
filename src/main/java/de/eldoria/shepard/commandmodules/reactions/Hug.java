package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Hug extends Reaction {

    public Hug() {
        super("hug",
                null,
                "command.reaction.description.hug",
                "command.reaction.command.otherHug",
                "command.reaction.command.hug");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getHug();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_HUG.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_HUG_SELF.tag;
    }
}
