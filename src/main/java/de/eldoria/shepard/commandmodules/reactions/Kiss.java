package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Kiss extends Reaction {

    public Kiss() {
        super("kiss",
                null,
                "command.reaction.description.kiss",
                "command.reaction.command.otherKiss",
                "command.reaction.command.kiss");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getKiss();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_KISS.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_KISS_SELF.tag;
    }
}
