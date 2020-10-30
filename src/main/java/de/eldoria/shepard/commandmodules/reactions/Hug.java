package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Hug extends Reaction {

    public Hug() {
        super("hug",
                null,
                ReactionLocale.DESCRIPTION_HUG.tag,
                ReactionLocale.C_HUG_OTHER.tag,
                ReactionLocale.C_HUG.tag);
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
