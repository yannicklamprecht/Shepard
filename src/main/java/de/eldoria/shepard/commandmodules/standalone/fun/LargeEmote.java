package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Emote;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to get up to five emotes as the possible largest image.
 */
public class LargeEmote extends Command implements ExecutableAsync {
    /**
     * Create new large emote command object.
     */
    public LargeEmote() {
        super("largeEmote",
                new String[]{"lemote"},
                "command.largeEmote.description",
                SubCommand.builder("largeEmote")
                        .addSubcommand(null,
                                Parameter.createInput("command.largeEmote.argument.emote", "command.largeEmote.argumentDescription.emote", true))
                        .build(),
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        List<Emote> emotes = wrapper.getMessage().get().getEmotes()
                .stream().distinct().collect(Collectors.toList());

        if (emotes.size() == 0) {
            MessageSender.sendSimpleError(ErrorType.NO_EMOTE_FOUND, wrapper);
            return;
        }

        for (Emote emote : emotes.subList(0, Math.min(emotes.size(), 5))) {
            File fileFromURL = FileHelper.getFileFromURL(emote.getImageUrl());
            if (fileFromURL != null) {
                wrapper.getMessageChannel().sendFile(fileFromURL).queue();
            }
        }
    }
}
