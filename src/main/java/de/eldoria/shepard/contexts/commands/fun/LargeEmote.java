package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.listener.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;

import java.util.List;
import java.util.stream.Collectors;

public class LargeEmote extends Command {
    public LargeEmote() {
        commandName = "largeEmote";
        commandDesc = "Get the image of the emote";
        commandArgs = new CommandArg[] {new CommandArg("Emote[...]", "Mention one or more custom emote!", true)};
        commandAliases = new String[] {"lemote"};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        List<Emote> emotes = dataWrapper.getMessage().getEmotes()
                .stream().distinct().collect(Collectors.toList());

        if (emotes.size() == 0) {
            MessageSender.sendSimpleError(ErrorType.NO_EMOTE_FOUND, dataWrapper.getChannel());
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        for (Emote emote : emotes.subList(0, Math.min(emotes.size(), 5))) {
            builder.setImage(emote.getImageUrl());
            dataWrapper.getChannel().sendMessage(builder.build()).queue();
        }
    }
}
