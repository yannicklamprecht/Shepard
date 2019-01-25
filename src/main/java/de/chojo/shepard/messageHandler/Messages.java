package de.chojo.shepard;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class Messages {
    public static void sendMessage(String message, MessageChannel channel) {
        String[] messageParts = message.split(System.lineSeparator());
        String messagePart = "";
        for (int i = 0; i < messageParts.length; i++) {
            if (messagePart.length() + messageParts[i].length() < 1024) {
                messagePart = messagePart + messageParts[i] + System.lineSeparator();
            }else{
                channel.sendMessage(messagePart).queue();
                messagePart = "";
                i--;
            }
        }
        channel.sendMessage(messagePart).queue();
    }

    public static void sendTextBox(String title, ArrayList<MessageEmbed.Field> fields, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        for (MessageEmbed.Field field : fields) {
            builder.addField(field);
        }
        builder.setFooter("by Shepard", "https://cdn.discordapp.com/avatars/512413049894731780/e7262c349f015c5f6f25e6bca8a689d0.png?size=1024");
        channel.sendMessage(builder.build()).queue();
    }

    public static void sendSimpleTextBox(String title, String description, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(description);
        builder.setFooter("by Shepard", "https://cdn.discordapp.com/avatars/512413049894731780/e7262c349f015c5f6f25e6bca8a689d0.png?size=1024");
        channel.sendMessage(builder.build()).queue();
    }

    public static void sendError(MessageEmbed.Field[] fields, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("ERROR!");
        for (MessageEmbed.Field field : fields) {
            builder.addField(field);
            builder.setColor(Color.red);
            builder.setFooter("by Shepard", "https://cdn.discordapp.com/avatars/512413049894731780/e7262c349f015c5f6f25e6bca8a689d0.png?size=1024");
            channel.sendMessage(builder.build()).queue();
        }
    }

    public static void deleteMessage(MessageReceivedEvent receivedEvent) {
        try {
            receivedEvent.getMessage().delete().submit();
        } catch (
                InsufficientPermissionException e) {
            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Lack of Permission", "Missing permission: MESSAGE_MANAGE", false)}, receivedEvent.getChannel());
        }
    }

    public static void LogMessageAsPlainText(MessageReceivedEvent event, MessageChannel channel){
        channel.sendMessage(event.getGuild().getName() + " | " + event.getMessage().getCategory().getName() + " | " + event.getMessage().getChannel().getName() + " by " + event.getAuthor().getName() + ": " + event.getMessage().getContentRaw()).queue();
    }

    public static void LogMessageAsEmbedded(MessageReceivedEvent event, MessageChannel channel){
        Instant instant = Instant.now(); // get The current time in instant object
        Timestamp t=java.sql.Timestamp.from(instant); // Convert instant to Timestamp

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(event.getGuild().getName() + " | " + event.getChannel().getName());
        builder.setTimestamp(t.toInstant());
        builder.setAuthor(event.getAuthor().getAsTag(), null,event.getAuthor().getAvatarUrl());
        builder.setDescription(event.getMessage().getContentRaw());
        channel.sendMessage(builder.build()).queue();
    }

    public static void sendImageBox(MessageReceivedEvent event) {

    }
}
