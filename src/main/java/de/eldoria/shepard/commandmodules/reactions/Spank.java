package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Spank extends Reaction {

    public Spank() {
        super("spank",
                null,
                "command.reaction.description.spank",
                "command.reaction.command.otherSpank",
                "command.reaction.command.spank");
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
