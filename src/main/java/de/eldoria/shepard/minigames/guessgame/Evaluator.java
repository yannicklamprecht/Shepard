package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.HentaiOrNotData;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.antlr.tool.BuildDependencyGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

public class Evaluator implements Runnable {
    private long messageId;
    private HentaiImage image;
    private long channelId;
    private Map<Long, Boolean> votes = new HashMap<>();

    Evaluator(Message message, HentaiImage image) {
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

        List<User> trueVotes = Verifier.getValidUserByLong(votes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList()));
        List<User> falseVotes = Verifier.getValidUserByLong(votes.entrySet().stream().filter(set -> !set.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()));

        List<User> winners = image.isHentai() ? trueVotes : falseVotes;
        List<User> looser = image.isHentai() ? falseVotes : trueVotes;

        int totalPlayer = looser.size() + winners.size();

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
        if (totalPlayer != 0) {

            List<User> firstWinner = winners.subList(0, Math.min(winners.size(), 5));

            String names = firstWinner.stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));

            String moreWinner = (winners.size() > 5
                    ? "...and " + (winners.size() - 5) + " more users!"
                    : "") + lineSeparator()
                    + "You earn " + votePoints + (votePoints > 1 ? " points!" : " point");

            builder.addField("Congratulation to:", names + moreWinner, false);
        } else {
            builder.setDescription("Nobody voted. owo");
        }

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

    public void addVote(User user, boolean voteValue) {
        votes.put(user.getIdLong(), voteValue);
    }

    public long getMessageId() {
        return messageId;
    }
}
