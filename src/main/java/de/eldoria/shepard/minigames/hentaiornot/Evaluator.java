package de.eldoria.shepard.minigames.hentaiornot;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.HentaiOrNotData;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

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
                System.out.println(emoji);
                if (emoji.contentEquals(Emoji.CHECK_MARK_BUTTON.unicode)) {
                    positiveAtomicVotes.set(reaction.retrieveUsers().complete());
                } else if (emoji.contentEquals(Emoji.CROSS_MARK.unicode)) {
                    negativeAtomicVotes.set(reaction.retrieveUsers().complete());
                }
            }
        });

        List<User> winners = image.isHentai() ? positiveAtomicVotes.get() : negativeAtomicVotes.get();
        List<User> looser = image.isHentai() ? negativeAtomicVotes.get() : positiveAtomicVotes.get();

        winners.removeIf(user -> user.getIdLong() == ShepardBot.getJDA().getSelfUser().getIdLong());
        looser.removeIf(user -> user.getIdLong() == ShepardBot.getJDA().getSelfUser().getIdLong());

        int votePoints = 1;
        if (winners.size() != 0) {
            float extraPoints = looser.size();
            float sharedPoints = extraPoints / winners.size();
            votePoints = Math.round(1 + sharedPoints);
        }

        HentaiOrNotData.addVoteScore(guildChannel.getGuild(),
                winners, votePoints, null);
        HentaiOrNotData.addVoteScore(guildChannel.getGuild(),
                looser, -1, null);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("It's " + (image.isHentai() ? "" : "not") + " a hentai image!");
        List<User> firstWinner = winners.subList(0, Math.min(winners.size(), 5));

        String names = firstWinner.stream().map(IMentionable::getAsMention)
                .collect(Collectors.joining(lineSeparator()));

        String moreWinner = (winners.size() > 5
                ? "...and " + (winners.size() - 5) + " more users!"
                : "") + lineSeparator()
                + "You earn " + votePoints + " points!";

        builder.addField("Congratulation to:", names + moreWinner, false);

        if (image.isHentai() && guildChannel.isNSFW()) {
            builder.setImage(image.getFullImage());
        } else if (image.isHentai() && !guildChannel.isNSFW()) {
            builder.addField("Image not displayed. This is not a NSFW Channel!", "", false);
        }
        if (!image.isHentai()) {
            builder.setImage(image.getFullImage());
        }

        guildChannel.sendMessage(builder.build()).queue();

        EvaluationScheduler.evaluationDone(channelId);
    }
}
