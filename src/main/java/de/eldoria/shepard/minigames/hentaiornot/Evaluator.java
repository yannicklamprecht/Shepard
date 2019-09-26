package de.eldoria.shepard.minigames.hentaiornot;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class Evaluator implements Runnable {
    private long messageId;
    private HentaiImage image;
    private long channelId;

    public Evaluator(Message message, HentaiImage image) {
        this.messageId = message.getIdLong();
        this.image = image;
        this.channelId = message.getChannel().getIdLong();
    }

    @Override
    public void run() {
        TextChannel guildChannel = ShepardBot.getJDA().getTextChannelById(channelId);
        if (guildChannel == null) {
            return;
        }

        Message message = guildChannel.retrieveMessageById(messageId).complete();
        List<MessageReaction> reactions = message.getReactions();

        AtomicReference<List<User>> positiveAtomicVotes = new AtomicReference<>(Collections.emptyList());
        AtomicReference<List<User>> negativeAtomicVotes = new AtomicReference<>(Collections.emptyList());

        reactions.forEach(reaction -> {
            if (reaction.getReactionEmote().isEmoji()) {
                String emoji = reaction.getReactionEmote().getEmoji();
                if (emoji.equals(Emoji.CHECK_MARK_BUTTON.unicode)) {
                    reaction.retrieveUsers().queue(positiveAtomicVotes::set);
                } else if (emoji.equals(Emoji.CROSS_MARK.unicode)) {
                    reaction.retrieveUsers().queue(negativeAtomicVotes::set);
                }
            }
        });

        List<User> positiveVotes = positiveAtomicVotes.get();
        List<User> negativeVotes = negativeAtomicVotes.get();


        float totalVotes = positiveVotes.size() + negativeVotes.size();
        float pointsForWinners = image.isHentai() ? totalVotes / positiveVotes.size() : totalVotes / negativeVotes.size();

        pointsForWinners = Math.min(1, pointsForWinners);
        pointsForWinners = Math.round(pointsForWinners);



        EvaluationScheduler.evaluationDone(channelId);
    }
}
