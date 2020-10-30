package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Blush extends Reaction {

    public Blush() {
        super("blush",
                null,
                ReactionLocale.DESCRIPTION_BLUSH.tag,
                ReactionLocale.C_BLUSH_OTHER.tag,
                ReactionLocale.C_BLUSH.tag);
    }

    @Override
    protected String[] getImages() {
        return getReactions().getBlush();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_BLUSH.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_BLUSH_SELF.tag;
    }
}
