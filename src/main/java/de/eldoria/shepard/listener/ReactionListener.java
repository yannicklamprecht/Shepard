package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.ReactionActionCollection;
import de.eldoria.shepard.minigames.guessgame.EvaluationScheduler;
import de.eldoria.shepard.minigames.guessgame.Evaluator;
import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReactionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        ReactionActionCollection.getInstance().invokeReactionAction(event);

        if (EvaluationScheduler.evaluationInProgress(event.getChannel())) {


            Evaluator channelEvaluator = EvaluationScheduler.getChannelEvaluator(event.getChannel());

            if (!event.getReaction().isSelf()
                    && event.getReaction().getReactionEmote().isEmoji()
                    && channelEvaluator != null
                    && channelEvaluator.getMessageId() == event.getMessageIdLong()) {

                String emoji = event.getReactionEmote().getEmoji();
                if (emoji.contentEquals(Emoji.CHECK_MARK_BUTTON.unicode)) {
                    channelEvaluator.addVote(event.getUser(), true);
                } else if (emoji.contentEquals(Emoji.CROSS_MARK.unicode)) {
                    channelEvaluator.addVote(event.getUser(), false);
                }

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
