package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Headpat extends Reaction {

    public Headpat() {
        super("headpat",
                new String[] {"pat"},
                "command.reaction.description.headpat",
                "command.reaction.command.otherHeadpat",
                "command.reaction.command.headpat");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getPat();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_HEADPAT.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_HEADPAT_SELF.tag;
    }
}
