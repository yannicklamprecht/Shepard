package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
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

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.A_FLAG;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.A_URL;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.C_ADD_IMAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.C_CANCEL_REGISTRATION;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.C_CHANGE_FLAG;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.C_REMOVE_IMAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.C_SHOW_IMAGE_SET;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.M_CHANGED_FLAG;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.M_DISPLAY_IMAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.M_REGISTRATION_CANCELED;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.M_REMOVED_IMAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.M_STARTED_REGISTRATION;
import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

public class GuessGameConfig extends Command {

    /**
     * Creates a new guess game config command object.
     */
    public GuessGameConfig() {
        commandName = "guessGameConfig";
        commandAliases = new String[] {"ggconfig"};
        commandDesc = DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("addImage", C_ADD_IMAGE.tag, true),
                        new SubArg("removeImage", C_REMOVE_IMAGE.tag, true),
                        new SubArg("changeFlag", C_CHANGE_FLAG.tag, true),
                        new SubArg("showImageSet", C_SHOW_IMAGE_SET.tag, true),
                        new SubArg("cancelRegistration", C_CANCEL_REGISTRATION.tag, true)),
                new CommandArg("values", false,
                        new SubArg("addImage", A_FLAG.tag),
                        new SubArg("removeImage", A_URL.tag),
                        new SubArg("changeFlag", A_URL + " "
                                + A_FLAG),
                        new SubArg("showImageSet", A_URL.tag),
                        new SubArg("cancelRegistration", A_EMPTY.tag))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArg arg = commandArgs[0];
        if (arg.isSubCommand(cmd,0)) {
            addImage(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd,1)) {
            removeImage(args, messageContext);
            return;
        }
        if (arg.isSubCommand(cmd,2)) {
            changeFlag(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd,3)) {
            showImageSet(args, messageContext);
            return;
        }
        if (arg.isSubCommand(cmd,4)) {
            ImageRegister.getInstance().cancelConfiguration(messageContext);
            MessageSender.sendMessage(M_REGISTRATION_CANCELED.tag, messageContext.getTextChannel());
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void showImageSet(String[] args, MessageEventDataWrapper messageContext) {
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
                .setTitle(fastLocaleAndReplace(M_DISPLAY_IMAGE.tag,
                        messageContext.getGuild(), hentaiImage.isNsfw() ? "NSFW" : "SFW"))
                .setThumbnail(hentaiImage.getCroppedImage())
                .setImage(hentaiImage.getFullImage())
                .setColor(hentaiImage.isNsfw() ? Color.red : Color.green);

        messageContext.getChannel().sendMessage(builder.build()).queue();
    }

    private void changeFlag(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[2], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        File fileFromURL = FileHelper.getFileFromURL(args[1]);

        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext.getTextChannel());
            return;
        }

        if (GuessGameData.changeImageFlag(args[1], booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage(fastLocaleAndReplace(M_CHANGED_FLAG.tag,
                    messageContext.getGuild(), booleanState.stateAsBoolean ? "NSFW" : "SFW"),
                    messageContext.getTextChannel());
            messageContext.getChannel().sendFile(fileFromURL).queue();
        }
    }

    private void removeImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        if (GuessGameData.removeHentaiImage(args[1], messageContext)) {
            MessageSender.sendMessage(M_REMOVED_IMAGE.tag, messageContext.getTextChannel());
        }
    }

    private void addImage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[1], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }
        ImageRegister.getInstance().startConfiguration(messageContext,
                booleanState.stateAsBoolean);
        MessageSender.sendMessage(M_STARTED_REGISTRATION.tag, messageContext.getTextChannel());
    }
}
