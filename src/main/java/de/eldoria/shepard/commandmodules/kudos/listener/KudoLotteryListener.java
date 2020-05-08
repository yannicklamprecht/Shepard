package de.eldoria.shepard.commandmodules.kudos.listener;

import de.eldoria.shepard.commandmodules.kudos.util.KudoLotteryEvaluator;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;

import static de.eldoria.shepard.util.Verifier.equalSnowflake;

public class KudoLotteryListener extends ListenerAdapter {
    private final ShardManager shardManager;
    private final ChannelEvaluator<KudoLotteryEvaluator> evaluator;

    /**
     * Create a new kudo lottery listener.
     *  @param shardManager       shardManager instance
     * @param evaluator evaluator for lottery
     */
    public KudoLotteryListener(ShardManager shardManager, ChannelEvaluator<KudoLotteryEvaluator> evaluator) {
        this.shardManager = shardManager;
        this.evaluator = evaluator;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (Verifier.equalSnowflake(event.getUser(), event.getJDA().getSelfUser())) return;
        UniqueMessageIdentifier identifier = UniqueMessageIdentifier.get(event);
        if (evaluator.isReactionMessage(identifier)) {
            KudoLotteryEvaluator channelEvaluator =
                    evaluator.getChannelEvaluator(event.getChannel());

            if (event.getReactionEmote().isEmoji() || channelEvaluator == null) {
                return;
            }

            Emote emote = event.getReaction().getReactionEmote().getEmote();

            if (equalSnowflake(emote, ShepardEmote.PLUS_X.getEmote(shardManager))) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 10);
            }
            if (equalSnowflake(emote, ShepardEmote.PLUS_I.getEmote(shardManager))) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 1);
            }
            if (equalSnowflake(emote, ShepardEmote.INFINITY.getEmote(shardManager))) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), -1);
            }

            event.getReaction().removeReaction(event.getUser()).queue();

        }
    }
}
