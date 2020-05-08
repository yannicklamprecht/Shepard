package de.eldoria.shepard.commandmodules.guessgame.listener;

import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameEvaluator;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;

import static de.eldoria.shepard.util.Verifier.equalSnowflake;

public class GuessGameListener extends ListenerAdapter implements ReqStatistics, ReqShardManager {
    private final ChannelEvaluator<GuessGameEvaluator> evaluator;
    private ShardManager shardManager;
    private Statistics statistics;

    /**
     * Create a new guess game listener.
     *
     * @param evaluator    evaluator for scheduler registration
     */
    public GuessGameListener(ChannelEvaluator<GuessGameEvaluator> evaluator) {
        this.shardManager = shardManager;
        this.evaluator = evaluator;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (Verifier.equalSnowflake(event.getUser(), event.getJDA().getSelfUser())) return;
        statistics.eventDispatched(event.getJDA());
        UniqueMessageIdentifier identifier = UniqueMessageIdentifier.get(event);
        if (evaluator.isReactionMessage(identifier)) {
            GuessGameEvaluator channelEvaluator = evaluator.getChannelEvaluator(event.getChannel());

            if (channelEvaluator == null || event.getReaction().getReactionEmote().isEmoji()) {
                return;
            }

            Emote emote = event.getReaction().getReactionEmote().getEmote();

            if (equalSnowflake(emote, ShepardEmote.ANIM_CHECKMARK.getEmote(shardManager))) {
                channelEvaluator.addVote(event.getUser(), true);
            } else if (equalSnowflake(emote, ShepardEmote.ANIM_CROSS.getEmote(shardManager))) {
                channelEvaluator.addVote(event.getUser(), false);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
