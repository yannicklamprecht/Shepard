package de.eldoria.shepard.commandmodules.guessgame.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.guessgame.data.GuessGameData;
import de.eldoria.shepard.commandmodules.guessgame.listener.GuessGameListener;
import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameEvaluator;
import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameImage;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;

import javax.sql.DataSource;
import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_SCORE;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_SCORE_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_START;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_TOP;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_TOP_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_GAME_DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_GAME_FOOTER;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_GLOBAL_RANKING;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_MINIGAME_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_ROUND_IN_PROGRESS;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_SCORE;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_SCORE_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_SERVER_RANKING;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.M_TITLE;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

/**
 * Command to start a guess game.
 * A started guess game will be manages by a {@link GuessGameEvaluator}.
 * Provides information about user scores.
 */
public class GuessGame extends Command implements Executable, ReqJDA, ReqDataSource, ReqInit {

    private ChannelEvaluator<GuessGameEvaluator> evaluator;
    private JDA jda;
    private DataSource source;
    private GuessGameData guessGameData;
    private GuessGameListener listener;

    /**
     * Create a new guess game command.
     */
    public GuessGame() {
        super("guessGame",
                new String[] {"gg", "nsfwornot"},
                GuessGameLocale.DESCRIPTION.tag,
                SubCommand.builder("guessGame")
                        .addSubcommand(C_SCORE.tag,
                                Parameter.createCommand("score"))
                        .addSubcommand(C_SCORE_GLOBAL.tag,
                                Parameter.createCommand("scoreGlobal"))
                        .addSubcommand(C_TOP.tag,
                                Parameter.createCommand("top"))
                        .addSubcommand(C_TOP_GLOBAL.tag,
                                Parameter.createCommand("topGlobal"))
                        .build(),
                C_START.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (!isArgument(messageContext.getChannel().getName(),
                "guessgame", "guess-game", "nsfwornot", "nsfw-or-not")) {
            MessageSender.sendMessage(M_MINIGAME_CHANNEL.tag, messageContext.getTextChannel());
            return;
        }
        if (args.length == 0) {
            startGame(messageContext);
            return;
        }
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        String cmd = args[0];

        if (isSubCommand(cmd, 0)) {
            int userScore = guessGameData.getUserScore(messageContext.getGuild(),
                    messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE + " **" + userScore + "**", messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 1)) {
            int userScore = guessGameData.getGlobalUserScore(messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE_GLOBAL + " **" + userScore, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 2)) {
            sendTopScores(false, messageContext);
        }
        if (isSubCommand(cmd, 3)) {
            sendTopScores(true, messageContext);
        }
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? guessGameData.getGlobalTopScore(10, messageContext, jda)
                : guessGameData.getTopScore(messageContext.getGuild(), 10, messageContext, jda);

        String rankTable = TextFormatting.getRankTable(ranks, messageContext);

        String ranking = global ? M_GLOBAL_RANKING.tag : M_SERVER_RANKING.tag;

        MessageSender.sendMessage("**" + ranking + "**" + lineSeparator() + rankTable, messageContext.getTextChannel());
    }

    private void startGame(MessageEventDataWrapper messageContext) {
        if (evaluator.isEvaluationActive(messageContext.getTextChannel())) {
            MessageSender.sendMessage(M_ROUND_IN_PROGRESS.tag, messageContext.getTextChannel());
            return;
        }

        GuessGameImage hentaiImage = guessGameData.getImage(messageContext);
        if (hentaiImage == null) {
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(M_TITLE.tag)
                .setDescription(localizeAllAndReplace(M_GAME_DESCRIPTION.tag, messageContext.getGuild(),
                        ShepardEmote.ANIM_CHECKMARK.getEmote(jda).getAsMention(),
                        ShepardEmote.ANIM_CROSS.getEmote(jda).getAsMention(),
                        "30"))
                .setImage(hentaiImage.getCroppedImage())
                .setFooter(M_GAME_FOOTER.tag);

        messageContext.getChannel().sendMessage(builder.build())
                .queue(message -> {
                    message.addReaction(ShepardEmote.ANIM_CHECKMARK.getEmote(jda)).queue();
                    message.addReaction(ShepardEmote.ANIM_CROSS.getEmote(jda)).queue();
                    evaluator.scheduleEvaluation(message, 30,
                            new GuessGameEvaluator(guessGameData, evaluator, jda, message, hentaiImage));
                });
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        evaluator = new ChannelEvaluator<>(5);
        guessGameData = new GuessGameData(source);
        jda.addEventListener(new GuessGameListener(jda, evaluator));
    }
}
