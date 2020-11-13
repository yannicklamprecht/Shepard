package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.localization.enums.commands.fun.ReactionLocale;

public class Lick extends Reaction {

    public Lick() {
        super("lick",
                null,
                "command.reaction.description.lick",
                "command.reaction.command.otherLick",
                "command.reaction.command.lick");
    }

    @Override
    protected String[] getImages() {
        return getReactions().getLick();
    }

    @Override
    protected String getOtherMessageLocaleCode() {
        return ReactionLocale.M_LICK.tag;
    }

    @Override
    protected String getSelfMessageLocaleCode() {
        return ReactionLocale.M_LICK_SELF.tag;
    }
}
