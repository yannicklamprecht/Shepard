package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Slap extends Reaction {

    public Slap() {
        super("slap",
                null,
                "command.reaction.description.slap",
                "command.reaction.command.otherSlap",
                "command.reaction.command.slap");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getSlap();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_SLAP.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_SLAP_SELF.tag;
    }
}
