package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.GuessGameData;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.BaseEvaluator;
import de.eldoria.shepard.util.Verifier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

public class GuessGameEvaluator extends BaseEvaluator {
    private final GuessGameImage image;
    private final Map<Long, Boolean> votes = new HashMap<>();

    public GuessGameEvaluator(Message message, GuessGameImage image) {
        super(message.getIdLong(), message.getChannel().getIdLong());
        this.image = image;
    }

    @Override
    public void run() {
        TextChannel guildChannel = ShepardBot.getJDA().getTextChannelById(channelId);
        if (guildChannel == null) {
            return;
        }

        List<User> trueVotes = Verifier.getValidUserByLong(votes.entrySet()
                .stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList()));
        List<User> falseVotes = Verifier.getValidUserByLong(votes.entrySet()
                .stream().filter(set -> !set.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()));

        List<User> winners = image.isHentai() ? trueVotes : falseVotes;
        List<User> looser = image.isHentai() ? falseVotes : trueVotes;


        int votePoints = 0;
        if (!winners.isEmpty()) {
            float extraPoints = looser.size();
            float sharedPoints = extraPoints / winners.size();
            votePoints = Math.round(1 + sharedPoints);
        }

        GuessGameData.addVoteScore(guildChannel.getGuild(),
                winners, votePoints, null);
        GuessGameData.addVoteScore(guildChannel.getGuild(),
                looser, -1, null);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("It's " + (image.isHentai() ? "" : "not") + " a hentai image!");
        int totalPlayer = looser.size() + winners.size();
        if (totalPlayer != 0 && !winners.isEmpty()) {



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

        Evaluator.getGuessGameScheduler().evaluationDone(guildChannel);
    }

    public void addVote(User user, boolean voteValue) {
        votes.put(user.getIdLong(), voteValue);
    }

    public long getMessageId() {
        return messageId;
    }
}
