package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.HentaiOrNotData;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.guessgame.EvaluationScheduler;
import de.eldoria.shepard.util.Emoji;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

import static de.eldoria.shepard.util.TextFormatting.fillString;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class GuessGame extends Command {

    private final String description = "Is this image part of an nsfw image or not?" + lineSeparator()
            + "Click :white_check_mark: for yes or :x: for no!" + lineSeparator()
            + "You have 30 seconds to guess!";

    private final String title = "NSFW or not! Guess now!";

    public GuessGame() {
        commandName = "guessGame";
        commandAliases = new String[] {"hentaiOrNot", "nsfwornot"};
        commandDesc = "Game where you have to guess if a cropped image is part of a hentai image or not.";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "Leave Empty to start a game." + lineSeparator()
                                + "**__s__core** -> Your score on this server" + lineSeparator()
                                + "**__g__lobal__s__core** -> Your global score." + lineSeparator()
                                + "**__t__op** -> The top 10 player on this server" + lineSeparator()
                                + "**__g__lobal__t__op** -> The top 10 player.", false)
        };
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (!isArgument(messageContext.getChannel().getName(), "hentaiornot", "hentai-or-not",
                "guessgame", "guess-game", "nsfwornot", "nsfw-or-not")) {
            MessageSender.sendMessage("This is a minigame."
                            + "Minigame commands can only be executed in a minigame channel." + lineSeparator()
                            + "Please create a channel with one of the following names to play the game:"
                            + lineSeparator()
                            + "`hentaiornot, hentai-or-not,guessgame, guess-game, nsfwornot, nsfw-or-not`",
                    messageContext.getChannel());
            return;
        }
        if (args.length == 0) {
            startGame(messageContext);
            return;
        }
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext.getChannel());
            return;
        }

        String cmd = args[0];

        if (isArgument(cmd, "score", "s")) {
            int userScore = HentaiOrNotData.getUserScore(messageContext.getGuild(),
                    messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage("Your score is: " + userScore, messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "globalScore", "gs")) {
            int userScore = HentaiOrNotData.getGlobalUserScore(messageContext.getAuthor(), messageContext);
            MessageSender.sendMessage("Your global score is: " + userScore, messageContext.getChannel());
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
                ? HentaiOrNotData.getGlobalTopScore(10, messageContext)
                : HentaiOrNotData.getTopScore(messageContext.getGuild(), 10, messageContext);

        int nameLength = 5;

        for (Rank rank : ranks) {
            nameLength = Math.max(nameLength, rank.getUser().getAsTag().length());
        }

        StringBuilder builder = new StringBuilder();

        builder.append(global ? "**GLOBAL RANKING**" : "**SERVER RANKING**")
                .append(lineSeparator()).append("```");
        builder.append("Rank ").append(fillString("User ", nameLength + 1)).append("Score");

        int ranking = 1;
        for (Rank rank : ranks) {

            builder.append(lineSeparator())
                    .append(fillString(ranking + "", 5))
                    .append(fillString(rank.getUser().getAsTag(), nameLength + 1))
                    .append(rank.getScore());
            ranking++;
        }

        builder.append(lineSeparator()).append("```");

        MessageSender.sendMessage(builder.toString(), messageContext.getChannel());
    }

    private void startGame(MessageEventDataWrapper messageContext) {
        if (EvaluationScheduler.evaluationInProgress(messageContext.getTextChannel())) {
            MessageSender.sendMessage("One round is still in progress.", messageContext.getChannel());
            return;
        }

        HentaiImage hentaiImage = HentaiOrNotData.getHentaiImage(messageContext);
        if (hentaiImage == null) {
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(title)
                .setDescription(description)
                .setImage(hentaiImage.getCroppedImage());

        messageContext.getChannel().sendMessage(builder.build())
                .queue(message -> {
                    message.addReaction(Emoji.CHECK_MARK_BUTTON.unicode).queue();
                    message.addReaction(Emoji.CROSS_MARK.unicode).queue();
                    EvaluationScheduler.scheduleEvaluation(message, hentaiImage);
                });
    }
}
