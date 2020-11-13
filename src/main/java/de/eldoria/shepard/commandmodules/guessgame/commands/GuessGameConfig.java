package de.eldoria.shepard.commandmodules.guessgame.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
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
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.sql.DataSource;
import java.awt.*;
import java.io.File;

import static de.eldoria.shepard.localization.enums.commands.admin.GuessGameConfigLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to configure the guess game.
 * Allows adding and removal of images.
 * Reflagging of a image is also possible.
 */
@CommandUsage(EventContext.GUILD)
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
                new String[]{"ggconfig"},
                "command.guessGameConfig.description",
                SubCommand.builder("guessGameConfig")
                        .addSubcommand("command.guessGameConfig.subcommand.addImage",
                                Parameter.createCommand("addImage"),
                                Parameter.createInput("command.guessGameConfig.argument.flag", "command.guessGameConfig.argumentDescription.flag", true))
                        .addSubcommand("command.guessGameConfig.subcommand.removeImage",
                                Parameter.createCommand("removeImage"),
                                Parameter.createInput("command.general.argument.url", "command.guessGameConfig.argumentDescription.url", true))
                        .addSubcommand("command.guessGameConfig.subcommand.changeFlag",
                                Parameter.createCommand("changeFlag"),
                                Parameter.createInput("command.general.argument.url", "command.guessGameConfig.argumentDescription.url", true),
                                Parameter.createInput("command.guessGameConfig.argument.flag", "command.guessGameConfig.argumentDescription.flag", true))
                        .addSubcommand("command.guessGameConfig.subcommand.showImageSet",
                                Parameter.createCommand("showImageSet"),
                                Parameter.createInput("command.general.argument.url", "command.guessGameConfig.argumentDescription.url", true))
                        .addSubcommand("command.guessGameConfig.subcommand.cancelRegistration",
                                Parameter.createCommand("cancelRegistration"))
                        .build(),
                CommandCategory.ADMIN);
        this.register = register;
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            addImage(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            removeImage(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 2)) {
            changeFlag(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            showImageSet(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 4)) {
            register.cancelConfiguration(wrapper);
            MessageSender.sendMessage(M_REGISTRATION_CANCELED.tag, wrapper.getMessageChannel());
            return;
        }
    }

    private void showImageSet(String[] args, EventWrapper wrapper) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        GuessGameImage image = guessGameData.getImage(args[1], wrapper);
        if (image == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, wrapper);
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(localizeAllAndReplace(M_DISPLAY_IMAGE.tag,
                        wrapper, image.isNsfw() ? "NSFW" : "SFW"))
                .setThumbnail(image.getCroppedImage())
                .setImage(image.getFullImage())
                .setColor(image.isNsfw() ? Color.red : Color.green);

        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    private void changeFlag(String[] args, EventWrapper wrapper) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[2], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
            return;
        }

        File fileFromURL = FileHelper.getFileFromURL(args[1]);

        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_IMAGE_URL, wrapper);
            return;
        }

        if (guessGameData.changeImageFlag(args[1], booleanState.stateAsBoolean, wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_CHANGED_FLAG.tag,
                    wrapper, booleanState.stateAsBoolean ? "NSFW" : "SFW"),
                    wrapper.getMessageChannel());
            wrapper.getMessageChannel().sendFile(fileFromURL).queue();
        }
    }

    private void removeImage(String[] args, EventWrapper wrapper) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        if (guessGameData.removeImage(args[1], wrapper)) {
            MessageSender.sendMessage(M_REMOVED_IMAGE.tag, wrapper.getMessageChannel());
        }
    }

    private void addImage(String[] args, EventWrapper wrapper) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        BooleanState booleanState = ArgumentParser.getBoolean(args[1], "nsfw", "sfw");
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
            return;
        }
        register.startConfiguration(wrapper,
                booleanState.stateAsBoolean);
        MessageSender.sendMessage(M_STARTED_REGISTRATION.tag, wrapper.getMessageChannel());
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
