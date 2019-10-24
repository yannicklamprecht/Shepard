package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.queries.GuessGameData;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.guessgame.ImageRegister;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.io.File;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class GuessGameConfig extends Command {

    public GuessGameConfig() {
        commandName = "guessGameConfig";
        commandAliases = new String[] {"ggc", "hentaiconfig"};
        commandDesc = "Manage Hentai Images";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__ddImage** -> Add image to database" + lineSeparator()
                                + "**__r__emoveImage** -> Remove image from database" + lineSeparator()
                                + "**__c__hange__F__lag** -> Set the NSFW flag for a image" + lineSeparator()
                                + "**__s__howImageSet** -> Display a image set." + lineSeparator()
                                + "**__c__ancel__R__egistration** -> Cancel registration of image", true),
                new CommandArg("values",
                        "**addImage** -> [`nsfw` if hentai |`sfw` if not]" + lineSeparator()
                                + "**removeImage** -> [url of cropped or full image]" + lineSeparator()
                                + "**changeFlag** -> [url of cropped or full image] [`nsfw` if hentai |`sfw` if not]"
                                + lineSeparator()
                                + "**showImageSet** -> [url of cropped or full image]" + lineSeparator()
                                + "**cancelRegistration** -> leave empty", false)
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "addImage", "a")) {
            addImage(args, messageContext);
            return;
        }

        if (isArgument(cmd, "removeImage", "r")) {
            removeImage(args, messageContext);
            return;
        }
        if (isArgument(cmd, "cancelRegistration", "cr")) {
            ImageRegister.getInstance().cancelConfiguration(messageContext);
            MessageSender.sendMessage("Registration canceled.", messageContext.getChannel());
            return;
        }
        if (isArgument(cmd, "changeFlag", "cf")) {
            changeFlag(args, messageContext);
            return;
        }

        if (isArgument(cmd, "showImageSet", "s")) {
            if (args.length != 2) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
                return;
            }
            GuessGameImage hentaiImage = GuessGameData.getHentaiImage(args[1], messageContext);
            if (hentaiImage == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext.getTextChannel());
                return;
            }

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Display " + (hentaiImage.isHentai() ? "hentai" : "non hentai")
                            + " image set.")
                    .setThumbnail(hentaiImage.getCroppedImage())
                    .setImage(hentaiImage.getFullImage())
                    .setColor(Color.green);

            messageContext.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void changeFlag(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[2], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }

        File fileFromURL = FileHelper.getFileFromURL(args[1]);

        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext.getTextChannel());
            return;
        }

        if (GuessGameData.changeImageFlag(args[1], booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage("Changed flag of image set to "
                    + (booleanState.stateAsBoolean ? "NSFW" : "SFW"), messageContext.getTextChannel());
            messageContext.getChannel().sendFile(fileFromURL).queue();
        }
    }

    private void removeImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }
        if (GuessGameData.removeHentaiImage(args[1], messageContext)) {
            MessageSender.sendMessage("Removed Image!", messageContext.getChannel());
        }
    }

    private void addImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[1], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
            return;
        }
        ImageRegister.getInstance().startConfiguration(messageContext,
                booleanState.stateAsBoolean);
        MessageSender.sendMessage("Started registration of new image set." + lineSeparator()
                + "Please send the cropped image.", messageContext.getChannel());
    }
}
