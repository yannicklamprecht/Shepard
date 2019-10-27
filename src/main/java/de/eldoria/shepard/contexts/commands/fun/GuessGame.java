package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.GuessGameData;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.localization.Util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.enums.fun.GuessGameLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.ChannelEvaluator;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.guessgame.GuessGameEvaluator;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.reactions.EmoteCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

import static de.eldoria.shepard.localization.enums.fun.GuessGameLocale.*;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class GuessGame extends Command {

    private static final String TITLE = "NSFW or not! Guess now!";

    public GuessGame() {
        commandName = "guessGame";
        commandAliases = new String[] {"nsfwornot"};
        commandDesc = "Game where you have to guess if a cropped image is part of a NSFW image or not.";
        commandArgs = new CommandArg[] {
                new CommandArg("action", false,
                        new SubArg("start game", C_START.replacement),
                        new SubArg("score", C_SCORE.replacement, true),
                        new SubArg("scoreGlobal", C_SCORE_GLOBAL.replacement, true),
                        new SubArg("top", C_TOP.replacement, true),
                        new SubArg("topGlobal", C_TOP_GLOBAL.replacement, true))
        };
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (!isArgument(messageContext.getChannel().getName(),
                "guessgame", "guess-game", "nsfwornot", "nsfw-or-not")) {
            MessageSender.sendMessage("This is a minigame."
                            + "Minigame commands can only be executed in a minigame channel." + lineSeparator()
                            + "Please create a channel with one of the following names to play the game:"
                            + lineSeparator()
                            + "`guessgame`, `guess-game` for the **sfw version**" + lineSeparator()
                            + "`nsfwornot`, `nsfw-or-not` for the **nsfw version**",
                    messageContext);
            return;
        }
        if (args.length == 0) {
            startGame(messageContext);
            return;
        }
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext);
            return;
        }

        String cmd = args[0];

        if (isArgument(cmd, "score", "s")) {
            int userScore = GuessGameData.getUserScore(messageContext.getGuild(),
                    messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE + " **" + userScore + "**", messageContext);
            return;
        }

        if (isArgument(cmd, "globalScore", "gs")) {
            int userScore = GuessGameData.getGlobalUserScore(messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE_GLOBAL + " **" + userScore, messageContext);
            return;
        }

        if (isArgument(cmd, "top", "t")) {
            sendTopScores(false, messageContext);
        }
        if (isArgument(cmd, "globalTop", "gt")) {
            sendTopScores(true, messageContext);
        }
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? GuessGameData.getGlobalTopScore(10, messageContext)
                : GuessGameData.getTopScore(messageContext.getGuild(), 10, messageContext);

        String rankTable = TextFormatting.getRankTable(ranks);

        String ranking = global ? M_GLOBAL_RANKING.replacement : M_SERVER_RANKING.replacement;

        MessageSender.sendMessage("**" + ranking + "**" + lineSeparator() + rankTable, messageContext);
    }

    private void startGame(MessageEventDataWrapper messageContext) {
        ChannelEvaluator<GuessGameEvaluator> channelEvaluator
                = Evaluator.getGuessGame();
        if (channelEvaluator.isEvaluationActive(messageContext.getTextChannel())) {
            MessageSender.sendMessage(M_ROUND_IN_PROGRESS.replacement, messageContext);
            return;
        }

        GuessGameImage hentaiImage = GuessGameData.getHentaiImage(messageContext);
        if (hentaiImage == null) {
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(M_TITLE.replacement)
                .setDescription(locale.getReplacedString(M_GAME_DESCRIPTION.localeCode, messageContext.getGuild(),
                        EmoteCollection.ANIM_CHECKMARK.getEmote().getAsMention(),
                        EmoteCollection.ANIM_CROSS.getEmote().getAsMention(),
                        "30"))
                .setImage(hentaiImage.getCroppedImage())
                .setFooter(M_GAME_FOOTER.replacement);

        messageContext.getChannel().sendMessage(builder.build())
                .queue(message -> {
                    message.addReaction(EmoteCollection.ANIM_CHECKMARK.getEmote()).queue();
                    message.addReaction(EmoteCollection.ANIM_CROSS.getEmote()).queue();
                    channelEvaluator.scheduleEvaluation(message, 30, new GuessGameEvaluator(message, hentaiImage));
                });
    }
}
