package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.GuessGameData;
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
import de.eldoria.shepard.util.reactions.EmoteCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

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
import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class GuessGame extends Command {

    public GuessGame() {
        commandName = "guessGame";
        commandAliases = new String[] {"nsfwornot"};
        commandDesc = GuessGameLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", false,
                        new SubArg("start game", C_START.tag),
                        new SubArg("score", C_SCORE.tag, true),
                        new SubArg("scoreGlobal", C_SCORE_GLOBAL.tag, true),
                        new SubArg("top", C_TOP.tag, true),
                        new SubArg("topGlobal", C_TOP_GLOBAL.tag, true))
        };
        category = ContextCategory.FUN;
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
        CommandArg arg = commandArgs[0];

        if (arg.isSubCommand(cmd, 1)) {
            int userScore = GuessGameData.getUserScore(messageContext.getGuild(),
                    messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE + " **" + userScore + "**", messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            int userScore = GuessGameData.getGlobalUserScore(messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage(M_SCORE_GLOBAL + " **" + userScore, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            sendTopScores(false, messageContext);
        }
        if (arg.isSubCommand(cmd, 4)) {
            sendTopScores(true, messageContext);
        }
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? GuessGameData.getGlobalTopScore(10, messageContext)
                : GuessGameData.getTopScore(messageContext.getGuild(), 10, messageContext);

        String rankTable = TextFormatting.getRankTable(ranks);

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
                .setDescription(fastLocaleAndReplace(M_GAME_DESCRIPTION.tag, messageContext.getGuild(),
                        EmoteCollection.ANIM_CHECKMARK.getEmote().getAsMention(),
                        EmoteCollection.ANIM_CROSS.getEmote().getAsMention(),
                        "30"))
                .setImage(hentaiImage.getCroppedImage())
                .setFooter(M_GAME_FOOTER.tag);

        messageContext.getChannel().sendMessage(builder.build())
                .queue(message -> {
                    message.addReaction(EmoteCollection.ANIM_CHECKMARK.getEmote()).queue();
                    message.addReaction(EmoteCollection.ANIM_CROSS.getEmote()).queue();
                    channelEvaluator.scheduleEvaluation(message, 30, new GuessGameEvaluator(message, hentaiImage));
                });
    }
}
