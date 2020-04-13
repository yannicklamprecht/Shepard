package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
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
public class LargeEmote extends Command {
    /**
     * Create new large emote command object.
     */
    public LargeEmote() {
        super("largeEmote",
                new String[] {"lemote"},
                DESCRIPTION.tag,
                SubCommand.builder("largeEmote")
                        .addSubcommand(DESCRIPTION.tag,
                                Parameter.createInput(A_EMOTE.tag, AD_EMOTE.tag, true))
                        .build(),
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
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
