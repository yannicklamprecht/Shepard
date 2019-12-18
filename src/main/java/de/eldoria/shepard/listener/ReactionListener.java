package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.ReactionActionCollection;
import de.eldoria.shepard.minigames.ChannelEvaluator;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import static de.eldoria.shepard.util.Verifier.equalSnowflake;

public class ReactionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }
        ReactionActionCollection.getInstance().invokeReactionAction(event);

        UniqueMessageIdentifier uniqueMessageIdentifier
                = new UniqueMessageIdentifier(event.getChannel(), event.getMessageIdLong());

        guessGame(event, uniqueMessageIdentifier);

        kudoCheck(event, uniqueMessageIdentifier);
    }

    private void guessGame(@Nonnull GuildMessageReactionAddEvent event,
                           UniqueMessageIdentifier uniqueMessageIdentifier) {
        ChannelEvaluator<GuessGameEvaluator> guessGameScheduler
                = Evaluator.getGuessGame();
        if (guessGameScheduler.isReactionMessage(uniqueMessageIdentifier)) {
            GuessGameEvaluator channelEvaluator = guessGameScheduler.getChannelEvaluator(event.getChannel());

            if (channelEvaluator == null || event.getReaction().getReactionEmote().isEmoji()) {
                return;
            }

            Emote emote = event.getReaction().getReactionEmote().getEmote();

            if (equalSnowflake(emote, ShepardEmote.ANIM_CHECKMARK.getEmote())) {
                channelEvaluator.addVote(event.getUser(), true);
            } else if (equalSnowflake(emote, ShepardEmote.ANIM_CROSS.getEmote())) {
                channelEvaluator.addVote(event.getUser(), false);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    private void kudoCheck(@Nonnull GuildMessageReactionAddEvent event,
                           UniqueMessageIdentifier uniqueMessageIdentifier) {
        ChannelEvaluator<KudoLotteryEvaluator> kudoLotteryScheduler
                = Evaluator.getKudoLotteryScheduler();
        if (kudoLotteryScheduler.isReactionMessage(uniqueMessageIdentifier)) {
            KudoLotteryEvaluator channelEvaluator =
                    kudoLotteryScheduler.getChannelEvaluator(event.getChannel());

            if (event.getReactionEmote().isEmoji() || channelEvaluator == null) {
                return;
            }

            Emote emote = event.getReaction().getReactionEmote().getEmote();

            if (equalSnowflake(emote, ShepardEmote.PLUS_X.getEmote())) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 10);
            }
            if (equalSnowflake(emote, ShepardEmote.PLUS_I.getEmote())) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 1);
            }
            if (equalSnowflake(emote, ShepardEmote.INFINITY.getEmote())) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), -1);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}
