package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.GuessGameData;
import de.eldoria.shepard.database.types.GuessGameImage;
import de.eldoria.shepard.localization.enums.GeneralLocale;
import de.eldoria.shepard.localization.enums.GuessGameConfigLocale;
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

public class GuessGameConfig extends Command {

    public GuessGameConfig() {
        commandName = "guessGameConfig";
        commandAliases = new String[] {"ggc", "hentaiconfig"};
        commandDesc = "Manage Hentai Images";
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("addImage", GuessGameConfigLocale.C_ADD_IMAGE.replacement, true),
                        new SubArg("removeImage", GuessGameConfigLocale.C_REMOVE_IMAGE.replacement, true),
                        new SubArg("changeFlag", GuessGameConfigLocale.C_CHANGE_FLAG.replacement, true),
                        new SubArg("showImageSet", GuessGameConfigLocale.C_SHOW_IMAGE_SET.replacement, true),
                        new SubArg("cancelRegistration", GuessGameConfigLocale.C_CANCEL_REGISTRATION.replacement, true)),
                new CommandArg("values", false,
                        new SubArg("addImage", GuessGameConfigLocale.C_FLAG.replacement),
                        new SubArg("removeImage", GuessGameConfigLocale.C_URL.replacement),
                        new SubArg("changeFlag", GuessGameConfigLocale.C_URL + " "
                                + GuessGameConfigLocale.C_FLAG),
                        new SubArg("showImageSet", GuessGameConfigLocale.C_URL.replacement),
                        new SubArg("cancelRegistration", GeneralLocale.EMPTY.replacement)),
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
            MessageSender.sendMessage(GuessGameConfigLocale.M_REGISTRATION_CANCELED.replacement, messageContext);
            return;
        }
        if (isArgument(cmd, "changeFlag", "cf")) {
            changeFlag(args, messageContext);
            return;
        }

        if (isArgument(cmd, "showImageSet", "s")) {
            if (args.length != 2) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
                return;
            }
            GuessGameImage hentaiImage = GuessGameData.getHentaiImage(args[1], messageContext);
            if (hentaiImage == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext);
                return;
            }

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(locale.getReplacedString(GuessGameConfigLocale.M_DISPLAY_IMAGE.localeCode,
                            messageContext.getGuild(), hentaiImage.isHentai() ? "NSFW" : "SFW"))
                    .setThumbnail(hentaiImage.getCroppedImage())
                    .setImage(hentaiImage.getFullImage())
                    .setColor(hentaiImage.isHentai() ? Color.red : Color.green);

            messageContext.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void changeFlag(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[2], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        File fileFromURL = FileHelper.getFileFromURL(args[1]);

        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext);
            return;
        }

        if (GuessGameData.changeImageFlag(args[1], booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(GuessGameConfigLocale.M_CHANGED_FLAG.localeCode,
                    messageContext.getGuild(), booleanState.stateAsBoolean ? "NSFW" : "SFW"), messageContext);
            messageContext.getChannel().sendFile(fileFromURL).queue();
        }
    }

    private void removeImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }
        if (GuessGameData.removeHentaiImage(args[1], messageContext)) {
            MessageSender.sendMessage(GuessGameConfigLocale.M_REMOVED_IMAGE.replacement, messageContext);
        }
    }

    private void addImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[1], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }
        ImageRegister.getInstance().startConfiguration(messageContext,
                booleanState.stateAsBoolean);
        MessageSender.sendMessage(GuessGameConfigLocale.M_STARTED_REGISTRATION.replacement, messageContext);
    }
}
