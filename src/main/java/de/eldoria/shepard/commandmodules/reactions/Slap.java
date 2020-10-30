package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Slap extends Reaction {

    public Slap() {
        super("slap",
                null, 
                ReactionLocale.DESCRIPTION_SLAP.tag, 
                ReactionLocale.C_SLAP_OTHER.tag,
                ReactionLocale.C_SLAP.tag);
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
