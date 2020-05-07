package de.eldoria.shepard.commandmodules.guessgame.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.guessgame.data.GuessGameData;
import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameImage;
import de.eldoria.shepard.commandmodules.guessgame.util.ImageRegister;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.sql.DataSource;
import java.awt.Color;
import java.io.File;

import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.AD_FLAG;
import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.AD_URL;
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
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to configure the guess game.
 * Allows adding and removal of images.
 * Reflagging of a image is also possible.
 */
public class GuessGameConfig extends Command implements ExecutableAsync, ReqDataSource, ReqInit {
    private final ImageRegister register;
    private DataSource source;
    private GuessGameData guessGameData;

    /**
     * Creates a new guess game config command object.
     *
     * @param register image register
     */
    public GuessGameConfig(ImageRegister register) {
        super("guessGameConfig",
                new String[] {"ggconfig"},
                DESCRIPTION.tag,
                SubCommand.builder("guessGameConfig")
                        .addSubcommand(C_ADD_IMAGE.tag,
                                Parameter.createCommand("addImage"),
                                Parameter.createInput(A_FLAG.tag, AD_FLAG.tag, true))
                        .addSubcommand(C_REMOVE_IMAGE.tag,
                                Parameter.createCommand("removeImage"),
                                Parameter.createInput(A_URL.tag, AD_URL.tag, true))
                        .addSubcommand(C_CHANGE_FLAG.tag,
                                Parameter.createCommand("changeFlag"),
                                Parameter.createInput(A_URL.tag, AD_URL.tag, true),
                                Parameter.createInput(A_FLAG.tag, AD_FLAG.tag, true))
                        .addSubcommand(C_SHOW_IMAGE_SET.tag,
                                Parameter.createCommand("showImageSet"),
                                Parameter.createInput(A_URL.tag, AD_URL.tag, true))
                        .addSubcommand(C_CANCEL_REGISTRATION.tag,
                                Parameter.createCommand("cancelRegistration"))
                        .build(),
                CommandCategory.ADMIN);
        this.register = register;
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            addImage(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            removeImage(args, messageContext);
            return;
        }
        if (isSubCommand(cmd, 2)) {
            changeFlag(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            showImageSet(args, messageContext);
            return;
        }
        if (isSubCommand(cmd, 4)) {
            register.cancelConfiguration(messageContext);
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
        GuessGameImage image = guessGameData.getImage(args[1], messageContext);
        if (image == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, messageContext.getTextChannel());
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(localizeAllAndReplace(M_DISPLAY_IMAGE.tag,
                        messageContext.getGuild(), image.isNsfw() ? "NSFW" : "SFW"))
                .setThumbnail(image.getCroppedImage())
                .setImage(image.getFullImage())
                .setColor(image.isNsfw() ? Color.red : Color.green);

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

        if (guessGameData.changeImageFlag(args[1], booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_FLAG.tag,
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
        if (guessGameData.removeImage(args[1], messageContext)) {
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
        register.startConfiguration(messageContext,
                booleanState.stateAsBoolean);
        MessageSender.sendMessage(M_STARTED_REGISTRATION.tag, messageContext.getTextChannel());
    }

    @Override
    public void init() {
        guessGameData = new GuessGameData(source);
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }
}
