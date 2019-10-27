package de.eldoria.shepard.messagehandler;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.database.types.GreetingSettings;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocale;

public final class MessageSender {


    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param messageContext messageContext
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, MessageEventDataWrapper messageContext) {
        sendTextBox(title, fields, messageContext, Color.gray);
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param messageContext messageContext
     * @param color   Color of the text box
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, MessageEventDataWrapper messageContext,
                                   Color color) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(fastLocale(title, messageContext))
                .setColor(color);
        for (LocalizedField field : fields) {
            builder.addField(field.getField());
        }
        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param reaction    Reaction for thumbnail
     * @param messageContext messageContext
     */
    public static void sendSimpleTextBox(String title, String description,
                                         ShepardReactions reaction, MessageEventDataWrapper messageContext) {
        sendSimpleTextBox(title, description, Color.gray, reaction, messageContext);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param messageContext messageContext
     */
    public static void sendSimpleTextBox(String title, String description, MessageEventDataWrapper messageContext) {
        sendSimpleTextBox(title, description, Color.gray, ShepardReactions.NONE, messageContext);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param messageContext messageContext
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         MessageEventDataWrapper messageContext) {
        sendSimpleTextBox(title, description, color, ShepardReactions.NONE, messageContext);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title          Title of text box
     * @param description    Text of textbox
     * @param color          Color of the text box
     * @param reaction       Reaction for thumbnail
     * @param messageContext channel to send
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         ShepardReactions reaction, MessageEventDataWrapper messageContext) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(fastLocale(title, messageContext))
                .setColor(color)
                .setDescription(fastLocale(description, messageContext));
        if (reaction != ShepardReactions.NONE) {
            builder.setThumbnail(reaction.thumbnail);
        }
        messageContext.getTextChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Sends a error with text box.
     *
     * @param fields  List of fields.
     * @param messageContext messageContext
     */
    public static void sendError(LocalizedField[] fields, MessageEventDataWrapper messageContext) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        for (LocalizedField field : fields) {
            builder.addField(field.getField())
                    .setColor(Color.red);
            messageContext.getTextChannel().sendMessage(builder.build()).queue();
        }
    }

    /**
     * Sends a simple error with predefined error messages.
     *
     * @param type    error type
     * @param messageContext messageContext
     */
    public static void sendSimpleError(ErrorType type, MessageEventDataWrapper messageContext) {
        if (type.isEmbed) {
            sendSimpleErrorEmbed(type.message, messageContext);
        } else {
            sendMessage(type.message, messageContext);
        }

    }

    /**
     * Sends a simple error to a channel.
     *
     * @param error   Error message
     * @param messageContext messageContext
     */
    public static void sendSimpleErrorEmbed(String error, MessageEventDataWrapper messageContext) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setDescription(error)
                .setColor(Color.red)
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        try {
            messageContext.getTextChannel().sendMessage(builder.build()).queue();
        } catch (ErrorResponseException e) {
            ShepardBot.getLogger().error(e.getMessage());
        }
    }

    /**
     * Deletes a received message.
     *
     * @param messageContext Event of message receive
     */
    public static void deleteMessage(MessageEventDataWrapper messageContext) {
        try {
            messageContext.getMessage().delete().submit();
        } catch (InsufficientPermissionException e) {
            MessageSender.sendError(new LocalizedField[] {new LocalizedField("Lack of Permission",
                    "Missing permission: MESSAGE_MANAGE", false, messageContext)}, messageContext);
        }
    }

    /**
     * Loggs a message es embed.
     *
     * @param messageContext messageContext to log
     */
    public static void logMessageAsEmbedded(MessageEventDataWrapper messageContext) {
        Instant instant = Instant.now(); // get The current time in instant object
        Timestamp t = java.sql.Timestamp.from(instant); // Convert instant to Timestamp

        if (messageContext.getChannel() instanceof TextChannel) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(messageContext.getGuild().getName() + " | " + messageContext.getChannel().getName())
                    .setTimestamp(t.toInstant())
                    .setAuthor(messageContext.getAuthor().getAsTag(), null, messageContext.getAuthor().getAvatarUrl())
                    .setDescription(messageContext.getMessage().getContentRaw());
            List<Message.Attachment> attachments = messageContext.getMessage().getAttachments();
            if (!attachments.isEmpty() && attachments.get(0).isImage()) {
                builder.setImage(attachments.get(0).getUrl());
            }

            try {
                messageContext.getTextChannel().sendMessage(builder.build()).queue();
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
                                    String source, TextChannel channel) {
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
        String command = receivedEvent.getAuthor().getAsTag()
                + " executed command \"" + label + " " + String.join(" ", args)
                + "\" on  guild " + receivedEvent.getGuild().getName() + " ("
                + receivedEvent.getGuild().getId() + ")";
        ShepardBot.getLogger().command(command);
        Normandy.getCommandLogChannel().sendMessage(command).queue();
    }

    /**
     * send a simple Message to a channel.
     *
     * @param message Message to send.
     * @param messageContext messageContext
     */
    public static void sendMessage(String message, MessageEventDataWrapper messageContext) {
        if (message.isEmpty()) return;

        String localizedMessage = fastLocale(message, messageContext);

        String[] messageParts = localizedMessage.split(System.lineSeparator());
        StringBuilder messagePart = new StringBuilder();
        for (int i = 0; i < messageParts.length; i++) {
            if (messagePart.length() + messageParts[i].length() < 1024) {
                messagePart.append(messageParts[i]).append(System.lineSeparator());
            } else {
                messageContext.getTextChannel().sendMessage(messagePart.toString()).queue();
                messagePart = new StringBuilder();
                i--;
            }
        }

        messageContext.getTextChannel().sendMessage(messagePart.toString()).queue();
    }

    /**
     * Sends a message to a channel. No auto localisation is provided.
     * @param message Message to send.
     * @param channel channel to send
     */
    public static void sendMessageToChannel(String message, MessageChannel channel) {
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
     * Sends a message to a user.
     *
     * @param user           User to send
     * @param attachments    Attachments to send
     * @param text           Text to send
     * @param messageContext message informations.
     */
    public static void sendAttachment(User user, List<Message.Attachment> attachments, String text,
                                      MessageEventDataWrapper messageContext) {
        user.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(text).queue();
            if (!attachments.isEmpty()) {
                for (Message.Attachment attachment : attachments) {

                    File fileFromURL = FileHelper.getFileFromURL(attachment.getUrl());
                    if (fileFromURL != null) {
                        privateChannel.sendFile(fileFromURL).queue();
                    } else {
                        MessageSender.sendSimpleErrorEmbed("File could not be loaded", messageContext);
                    }
                }
            }
        });
    }
}
