package de.eldoria.shepard.basemodules.reactionactions;

import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionActionListener extends ListenerAdapter implements ReqReactionAction {
    private ReactionActionCollection reactionAction;

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }
        reactionAction.invokeReactionAction(event);
    }

    @Override
    public void addReactionAction(ReactionActionCollection reactionAction) {
        this.reactionAction = reactionAction;
    }
}
