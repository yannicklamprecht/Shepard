package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Cry extends Reaction {

    public Cry() {
        super("cry",
                new String[]{"sad"},
                "command.reaction.description.cry",
                "command.reaction.command.otherCry",
                "command.reaction.command.cry");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getCry();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_CRY.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_CRY_SELF.tag;
    }
}
