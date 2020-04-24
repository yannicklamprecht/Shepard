package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Emote;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.fun.LargeEmoteLocale.AD_EMOTE;
import static de.eldoria.shepard.localization.enums.commands.fun.LargeEmoteLocale.A_EMOTE;
import static de.eldoria.shepard.localization.enums.commands.fun.LargeEmoteLocale.DESCRIPTION;

/**
 * Command to get up to five emotes as the possible largest image.
 */
public class LargeEmote extends Command implements ExecutableAsync {
    /**
     * Create new large emote command object.
     */
    public LargeEmote() {
        super("largeEmote",
                new String[] {"lemote"},
                DESCRIPTION.tag,
                SubCommand.builder("largeEmote")
                        .addSubcommand(null,
                                Parameter.createInput(A_EMOTE.tag, AD_EMOTE.tag, true))
                        .build(),
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<Emote> emotes = messageContext.getMessage().getEmotes()
                .stream().distinct().collect(Collectors.toList());

        if (emotes.size() == 0) {
            MessageSender.sendSimpleError(ErrorType.NO_EMOTE_FOUND, messageContext.getTextChannel());
            return;
        }

        for (Emote emote : emotes.subList(0, Math.min(emotes.size(), 5))) {
            File fileFromURL = FileHelper.getFileFromURL(emote.getImageUrl());
            if (fileFromURL != null) {
                messageContext.getChannel().sendFile(fileFromURL).queue();
            }
        }
    }
}
