package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.HentaiOrNotData;
import de.eldoria.shepard.database.types.HentaiImage;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.hentaiornot.EvaluationScheduler;
import de.eldoria.shepard.util.Emoji;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.ScheduledExecutorService;

import static java.lang.System.lineSeparator;

public class HentaiOrNot extends Command {

    public HentaiOrNot() {
        commandName = "hentaiOrNot";
        commandAliases = new String[] {"hentaigame"};
        commandDesc = "Game where you have to guess if a cropped image is part of a hentai image or not.";
        commandArgs = new CommandArg[0];
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            if (EvaluationScheduler.evaluationInProgress(messageContext.getTextChannel())) {
                MessageSender.sendMessage("One round is still in progress.", messageContext.getChannel());
                return;
            }

            HentaiImage hentaiImage = HentaiOrNotData.getHentaiImage(messageContext);
            if (hentaiImage == null) {
                return;
            }
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Hentai or not! Guess now!")
                    .setDescription("Is this image part of an hentai image or not?" + lineSeparator()
                            + "Click :white_check_mark: for yes or :x: for no!" + lineSeparator()
                            + "You have 30 seconds to guess!")
                    .setImage(hentaiImage.getCroppedImage());

            messageContext.getChannel().sendMessage(builder.build())
                    .queue(message -> {
                        message.addReaction(Emoji.CHECK_MARK_BUTTON.unicode).queue();
                        message.addReaction(Emoji.CROSS_MARK.unicode).queue();
                        EvaluationScheduler.scheduleEvaluation(message, hentaiImage);
                    });
        }

    }
}
