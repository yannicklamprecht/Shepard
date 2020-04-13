package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.database.queries.commands.GuessGameData;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.ChannelEvaluator;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_SCORE;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_SCORE_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.GuessGameLocale.C_START;
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
public class GuessGame extends Command {

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
                        .addSubcommand(C_SCORE.tag,
                                Parameter.createCommand("top"))
                        .addSubcommand(C_SCORE_GLOBAL.tag,
                                Parameter.createCommand("topGlobal"))
                        .build(),
                C_START.tag,
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
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
        SubCommand arg = subCommands[0];

        if (isSubCommand(cmd, 1)) {
            int userScore = GuessGameData.getUserScore(messageContext.getGuild(),
                    messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE + " **" + userScore + "**", messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 2)) {
            int userScore = GuessGameData.getGlobalUserScore(messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE_GLOBAL + " **" + userScore, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 3)) {
            sendTopScores(false, messageContext);
        }
        if (isSubCommand(cmd, 4)) {
            sendTopScores(true, messageContext);
        }
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? GuessGameData.getGlobalTopScore(10, messageContext)
                : GuessGameData.getTopScore(messageContext.getGuild(), 10, messageContext);

        String rankTable = TextFormatting.getRankTable(ranks, messageContext);

        String ranking = global ? M_GLOBAL_RANKING.tag : M_SERVER_RANKING.tag;

        MessageSender.sendMessage("**" + ranking + "**" + lineSeparator() + rankTable, messageContext.getTextChannel());
    }

    private void startGame(MessageEventDataWrapper messageContext) {
        ChannelEvaluator<GuessGameEvaluator> channelEvaluator
                = Evaluator.getGuessGame();
        if (channelEvaluator.isEvaluationActive(messageContext.getTextChannel())) {
            MessageSender.sendMessage(M_ROUND_IN_PROGRESS.tag, messageContext.getTextChannel());
            return;
        }

        GuessGameImage hentaiImage = GuessGameData.getHentaiImage(messageContext);
        if (hentaiImage == null) {
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(M_TITLE.tag)
                .setDescription(localizeAllAndReplace(M_GAME_DESCRIPTION.tag, messageContext.getGuild(),
                        ShepardEmote.ANIM_CHECKMARK.getEmote().getAsMention(),
                        ShepardEmote.ANIM_CROSS.getEmote().getAsMention(),
                        "30"))
                .setImage(hentaiImage.getCroppedImage())
                .setFooter(M_GAME_FOOTER.tag);

        messageContext.getChannel().sendMessage(builder.build())
                .queue(message -> {
                    message.addReaction(ShepardEmote.ANIM_CHECKMARK.getEmote()).queue();
                    message.addReaction(ShepardEmote.ANIM_CROSS.getEmote()).queue();
                    channelEvaluator.scheduleEvaluation(message, 30, new GuessGameEvaluator(message, hentaiImage));
                });
    }
}
