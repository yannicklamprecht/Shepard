package de.eldoria.shepard.commandmodules.guessgame.util;

import de.eldoria.shepard.commandmodules.guessgame.data.GuessGameData;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.BaseEvaluator;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_GAME_DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_GAME_FOOTER;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_ROUND_IN_PROGRESS;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_TITLE;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_CONGRATULATION;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_EARN;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_IMAGE_NOT_DISPLAYED;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_MORE_USER;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_NO_WINNER;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_TITLE_NSFW;
import static de.eldoria.shepard.localization.enums.minigames.GuessGameEvaluatorLocale.M_TITLE_SFW;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

public class GuessGameEvaluator extends BaseEvaluator {
    private GuessGameImage image;
    private final Guild guild;
    private TextChannel channel;
    private final Map<Long, Boolean> votes = new HashMap<>();
    private final GuessGameData guessGameData;
    private final ChannelEvaluator<GuessGameEvaluator> evaluator;
    private final ShardManager shardManager;

    /**
     * Creates a new guess game evaluator.
     *
     * @param guessGameData guess game data
     * @param evaluator     evaluator for guess games
     * @param shardManager  shardManager instance
     */
    public GuessGameEvaluator(GuessGameData guessGameData, ChannelEvaluator<GuessGameEvaluator> evaluator, ShardManager shardManager,
                              Guild guild, TextChannel channel) {
        super(channel.getIdLong());
        this.guessGameData = guessGameData;
        this.evaluator = evaluator;
        this.shardManager = shardManager;
        this.guild = guild;
        this.channel = channel;
    }

    @Override
    public void run() {
        TextChannel guildChannel = shardManager.getTextChannelById(channelId);
        if (guildChannel == null) {
            return;
        }

        List<User> trueVotes = Verifier.getValidUserByLong(shardManager, votes.entrySet()
                .stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList()));
        List<User> falseVotes = Verifier.getValidUserByLong(shardManager, votes.entrySet()
                .stream().filter(set -> !set.getValue()).map(Map.Entry::getKey).collect(Collectors.toList()));

        List<User> winners = image.isNsfw() ? trueVotes : falseVotes;
        List<User> looser = image.isNsfw() ? falseVotes : trueVotes;


        int votePoints = 0;
        if (!winners.isEmpty()) {
            float extraPoints = looser.size();
            float sharedPoints = extraPoints / winners.size();
            votePoints = Math.round(1 + sharedPoints);
        }

        guessGameData.addVoteScore(guildChannel.getGuild(),
                winners, votePoints, null);
        guessGameData.addVoteScore(guildChannel.getGuild(),
                looser, -1, null);

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(guildChannel.getGuild());
        builder.setTitle(image.isNsfw() ? M_TITLE_NSFW.tag : M_TITLE_SFW.tag);
        int totalPlayer = looser.size() + winners.size();
        if (totalPlayer != 0 && !winners.isEmpty()) {


            List<User> firstWinner = winners.subList(0, Math.min(winners.size(), 5));

            String names = firstWinner.stream().map(IMentionable::getAsMention)
                    .collect(Collectors.joining(lineSeparator()));
            String moreWinner = (winners.size() > 5
                    ? lineSeparator() + TextLocalizer.localizeAllAndReplace(M_MORE_USER.tag, guildChannel.getGuild(),
                    winners.size() - 5 + "")
                    : "") + lineSeparator()
                    + M_EARN + " " + votePoints + " " + (votePoints > 1 ? WordsLocale.POINTS : WordsLocale.POINT);

            builder.addField(M_CONGRATULATION.tag, names + moreWinner, false);
        } else {
            builder.setDescription(M_NO_WINNER.tag);
        }

        guildChannel.getManager().setNSFW(guildChannel.getName().startsWith("nsfw")).complete();

        boolean nsfw = guildChannel.isNSFW();

        if (image.isNsfw() && nsfw) {
            builder.setImage(image.getFullImage());
        } else if (image.isNsfw() && !nsfw) {
            builder.addField(M_IMAGE_NOT_DISPLAYED.tag, "", false);
        }
        if (!image.isNsfw()) {
            builder.setImage(image.getFullImage());
        }

        guildChannel.sendMessage(builder.build()).complete();

        evaluator.evaluationDone(guildChannel);

        if (!votes.isEmpty()) {
            evaluator.scheduleEvaluation(30,
                    new GuessGameEvaluator(guessGameData, evaluator, shardManager, guild, channel));
        }
    }

    @Override
    public Optional<Message> start() {
        if (evaluator.isEvaluationActive(channel)) {
            MessageSender.sendMessage(M_ROUND_IN_PROGRESS.tag, channel);
            return Optional.empty();
        }

        image = guessGameData.getImage(null);
        if (image == null) {
            return Optional.empty();
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder()
                .setTitle(M_TITLE.tag)
                .setDescription(localizeAllAndReplace(M_GAME_DESCRIPTION.tag, guild,
                        ShepardEmote.ANIM_CHECKMARK.getEmote(shardManager).getAsMention(),
                        ShepardEmote.ANIM_CROSS.getEmote(shardManager).getAsMention(),
                        "30"))
                .setImage(image.getCroppedImage())
                .setFooter(M_GAME_FOOTER.tag);

        Message complete = channel.sendMessage(builder.build()).complete();

        complete.addReaction(ShepardEmote.ANIM_CHECKMARK.getEmote(shardManager)).queue();
        complete.addReaction(ShepardEmote.ANIM_CROSS.getEmote(shardManager)).queue();
        return Optional.of(complete);
    }

    /**
     * Adds a vote or overrides the old vote.
     *
     * @param user      voted user
     * @param voteValue vote value
     */
    public void addVote(User user, boolean voteValue) {
        votes.put(user.getIdLong(), voteValue);
    }
}
