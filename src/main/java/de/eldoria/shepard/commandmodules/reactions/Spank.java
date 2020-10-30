package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Spank extends Reaction {

    public Spank() {
        super("spank",
                null, 
                ReactionLocale.DESCRIPTION_SPANK.tag, 
                ReactionLocale.C_SPANK_OTHER.tag,
                ReactionLocale.C_SPANK.tag);
    }

    @Override
    protected String[] getImages() {
        return getReactions().getSpank();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_SPANK.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_SPANK_SELF.tag;
    }
}
