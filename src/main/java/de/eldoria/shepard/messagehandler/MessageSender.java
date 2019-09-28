package de.eldoria.shepard.messagehandler;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.types.GreetingSettings;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class MessageSender {

    /**
     * send a simple Message to a channel.
     *
     * @param message Message to send.
     * @param channel channel to send
     */
    public static void sendMessage(String message, MessageChannel channel) {
        if (message.isEmpty()) return;

        String[] messageParts = message.split(System.lineSeparator());
        StringBuilder messagePart = new StringBuilder();
        for (int i = 0; i < messageParts.length; i++) {
            if (messagePart.length() + messageParts[i].length() < 1024) {
                messagePart.append(messageParts[i]).append(System.lineSeparator());
            } else {
                channel.sendMessage(messagePart.toString()).queue();
                messagePart = new StringBuilder();
                i--;
            }
        }

        channel.sendMessage(messagePart.toString()).queue();
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel to send.
     */
    public static void sendTextBox(String title, List<MessageEmbed.Field> fields, MessageChannel channel) {
        sendTextBox(title, fields, channel, Color.gray);
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel to send.
     * @param color   Color of the text box
     */
    public static void sendTextBox(String title, List<MessageEmbed.Field> fields, MessageChannel channel, Color color) {
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription("test")
                .setTitle(title)
                .setColor(color);
        for (MessageEmbed.Field field : fields) {
            builder.addField(field);
        }
        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param reaction    Reaction for thumbnail
     * @param channel     channel to send
     */
    public static void sendSimpleTextBox(String title, String description,
                                         ShepardReactions reaction, MessageChannel channel) {
        sendSimpleTextBox(title, description, Color.gray, reaction, channel);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param channel     channel to send
     */
    public static void sendSimpleTextBox(String title, String description, MessageChannel channel) {
        sendSimpleTextBox(title, description, Color.gray, ShepardReactions.NONE, channel);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param channel     channel to send
     */
    public static void sendSimpleTextBox(String title, String description, Color color, MessageChannel channel) {
        sendSimpleTextBox(title, description, color, ShepardReactions.NONE, channel);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param reaction    Reaction for thumbnail
     * @param channel     channel to send
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         ShepardReactions reaction, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setDescription(description);
        if (reaction != ShepardReactions.NONE) {
            builder.setThumbnail(reaction.thumbnail);
        }
        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Sends a error with text box.
     *
     * @param fields  List of fields.
     * @param channel channel to send.
     */
    public static void sendError(MessageEmbed.Field[] fields, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        for (MessageEmbed.Field field : fields) {
            builder.addField(field)
                    .setColor(Color.red);
            channel.sendMessage(builder.build()).queue();
        }
    }

    /**
     * Sends a simple error with predefined error messages.
     *
     * @param type    error type
     * @param channel channel to send
     */
    public static void sendSimpleErrorEmbed(ErrorType type, MessageChannel channel) {
        if (type.isEmbed) {
            sendSimpleErrorEmbed(type.message, channel);
        } else {
            sendMessage(type.message, channel);
        }

    }

    /**
     * Sends a simple error to a channel.
     *
     * @param error   Error message
     * @param channel channel to send
     */
    public static void sendSimpleErrorEmbed(String error, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setDescription(error)
                .setColor(Color.red)
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        try {
            channel.sendMessage(builder.build()).queue();
        } catch (ErrorResponseException e) {
            ShepardBot.getLogger().error(e.getMessage());
        }
    }

    /**
     * Deletes a received message.
     *
     * @param receivedEvent Event of message receive
     */
    public static void deleteMessage(MessageEventDataWrapper receivedEvent) {
        try {
            receivedEvent.getMessage().delete().submit();
        } catch (InsufficientPermissionException e) {
            MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Lack of Permission",
                    "Missing permission: MESSAGE_MANAGE", false)}, receivedEvent.getChannel());
        }
    }

    /**
     * Loggs a message in plain text.
     *
     * @param messageContext messageContext to log
     * @param channel        channel to log
     */
    public static void logMessageAsPlainText(MessageEventDataWrapper messageContext, MessageChannel channel) {
        channel.sendMessage(messageContext.getGuild().getName() + " | "
                + Objects.requireNonNull(messageContext.getMessage().getCategory()).getName()
                + " | " + messageContext.getMessage().getChannel().getName() + " by "
                + messageContext.getAuthor().getName()
                + ": " + messageContext.getMessage().getContentRaw()).queue();
    }

    /**
     * Loggs a message es embed.
     *
     * @param messageContext messageContext to log
     * @param channel        channel to log
     */
    public static void logMessageAsEmbedded(MessageEventDataWrapper messageContext, MessageChannel channel) {
        Instant instant = Instant.now(); // get The current time in instant object
        Timestamp t = java.sql.Timestamp.from(instant); // Convert instant to Timestamp

        if (messageContext.getChannel() instanceof TextChannel) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(messageContext.getGuild().getName() + " | " + messageContext.getChannel().getName());
            builder.setTimestamp(t.toInstant());
            builder.setAuthor(messageContext.getAuthor().getAsTag(), null, messageContext.getAuthor().getAvatarUrl());
            builder.setDescription(messageContext.getMessage().getContentRaw());
            try {
                channel.sendMessage(builder.build()).queue();
            } catch (InsufficientPermissionException e) {
                //Only when beta bot is running.
            }
        }
    }

    /**
     * sends a greeting text.
     *
     * @param event    event to log
     * @param channel  channel to log
     * @param source   invite source
     * @param greeting Greeting object
     */
    public static void sendGreeting(GuildMemberJoinEvent event, GreetingSettings greeting,
                                    String source, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        if (source != null) {
            builder.setFooter("Joined via " + source);
        }
        User user = event.getUser();
        String message = Replacer.applyUserPlaceholder(user, greeting.getText());
        builder.addField(event.getUser().getAsTag(),
                message, true)
                .setColor(Color.green)
                .setThumbnail(event.getUser().getAvatarUrl() == null
                        ? ShepardReactions.EXCITED.thumbnail
                        : event.getUser().getAvatarUrl());
        channel.sendMessage(builder.build()).queue();
    }

    public static void logCommand(String label, String[] args, MessageEventDataWrapper receivedEvent) {
        String command = "Executed command \"" + label + " " + String.join(" ", args)
                + "\" on  guild " + receivedEvent.getGuild().getName() + " ("
                + receivedEvent.getGuild().getId() + ")";
        ShepardBot.getLogger().command(command);
    }
}
