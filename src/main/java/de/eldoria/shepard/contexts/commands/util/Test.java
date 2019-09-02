package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A test command without specified behaviour.
 */
public class Test extends Command {

    public Test() {
        commandName = "test";
        commandDesc = "Testcommand!";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        EmbedBuilder builder = new EmbedBuilder();

        List<MessageEmbed.Field> fields = new ArrayList<>();

        builder.setImage(ShepardBot.getJDA().getSelfUser().getAvatarUrl());

        fields.add(new MessageEmbed.Field("Name of Field 1 - inline", "Desc of field 1 - inline", true));
        fields.add(new MessageEmbed.Field("Name of Field 2 - inline", "Desc of field 2 - inline", true));
        fields.add(new MessageEmbed.Field("Name of Field 3 - inline", "Desc of field 3 - inline", true));
        fields.add(new MessageEmbed.Field("Name of Field 4 - not inline", "Desc of field 4 - not inline", false));
        fields.add(new MessageEmbed.Field("Name of Field 5 - not inline", "Desc of field 5 - not inline" + System.lineSeparator() + "Image below", false));

        builder.setAuthor("Sheard Beta bot test", ShepardBot.getJDA().getSelfUser().getAvatarUrl(),ShepardBot.getJDA().getSelfUser().getAvatarUrl());
        builder.setThumbnail(ShepardBot.getJDA().getSelfUser().getAvatarUrl());
        builder.setDescription("Embed description. Thumbnail on the right.");
        builder.setTitle("Testembed");
        builder.setColor(Color.red);
        for (MessageEmbed.Field field : fields) {
            builder.addField(field);
        }
        builder.setFooter("by Shepard", ShepardBot.getJDA().getSelfUser().getAvatarUrl());
        receivedEvent.getChannel().sendMessage(builder.build()).queue();
    }
}
