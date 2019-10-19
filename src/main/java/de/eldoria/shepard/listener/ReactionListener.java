package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.ReactionActionCollection;
import de.eldoria.shepard.collections.UniqueMessageIdentifier;
import de.eldoria.shepard.minigames.EvaluationScheduler;
import de.eldoria.shepard.minigames.EvaluationSchedulerCollection;
import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;
import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReaction().isSelf() || !event.getReaction().getReactionEmote().isEmoji()) {
            return;
        }

        String emoji = event.getReactionEmote().getEmoji();

        ReactionActionCollection.getInstance().invokeReactionAction(event);
        UniqueMessageIdentifier uniqueMessageIdentifier = new UniqueMessageIdentifier(event.getChannel(), event.getMessageIdLong());
        EvaluationScheduler<GuessGameEvaluator> guessGameScheduler = EvaluationSchedulerCollection.getGuessGameScheduler();
        if (guessGameScheduler.isReactionMessage(uniqueMessageIdentifier)) {
            GuessGameEvaluator channelEvaluator = guessGameScheduler.getChannelEvaluator(event.getChannel());

            if (channelEvaluator == null) {
                return;
            }
            if (emoji.contentEquals(Emoji.CHECK_MARK_BUTTON.unicode)) {
                channelEvaluator.addVote(event.getUser(), true);
            } else if (emoji.contentEquals(Emoji.CROSS_MARK.unicode)) {
                channelEvaluator.addVote(event.getUser(), false);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }

        EvaluationScheduler<KudoLotteryEvaluator> kudoLotteryScheduler = EvaluationSchedulerCollection.getKudoLotteryScheduler();
        if (kudoLotteryScheduler.isReactionMessage(uniqueMessageIdentifier)) {
            KudoLotteryEvaluator channelEvaluator = kudoLotteryScheduler.getChannelEvaluator(event.getChannel());

            if (channelEvaluator == null) {
                return;
            }

            if (emoji.contentEquals(Emoji.MONEY_BAG.unicode)) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 10);
            }
            if (emoji.contentEquals(Emoji.DOLLAR.unicode)) {
                channelEvaluator.addBet(event.getGuild(), event.getUser(), 1);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }


    }
}
