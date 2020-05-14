package de.eldoria.shepard.basemodules.reactionactions;

import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionActionListener extends ListenerAdapter implements ReqReactionAction, ReqStatistics {
    private ReactionActionCollection reactionAction;
    private Statistics statistics;

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }
        statistics.eventDispatched(event.getJDA());
        reactionAction.invokeReactionAction(EventWrapper.wrap(event));
    }

    @Override
    public void onPrivateMessageReactionAdd(@Nonnull PrivateMessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }
        statistics.eventDispatched(event.getJDA());
        reactionAction.invokeReactionAction(EventWrapper.wrap(event));
    }

    @Override
    public void addReactionAction(ReactionActionCollection reactionAction) {
        this.reactionAction = reactionAction;
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
