package de.eldoria.shepard.messagehandler;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.database.types.GreetingSettings;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.util.Replacer;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAll;
import static java.lang.System.lineSeparator;

public final class MessageSender {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM. HH:mm:ss");

    private static String timestamp() {
        return "[" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + "]";
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, TextChannel channel) {
        sendTextBox(title, fields, channel, Color.gray);
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel
     * @param color   Color of the text box
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, TextChannel channel,
                                   Color color) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(TextLocalizer.localizeAll(title, channel))
                .setColor(color);
        for (LocalizedField field : fields) {
            builder.addField(field.getField());
        }
        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param reaction    Reaction for thumbnail
     * @param channel     channel
     */
    public static void sendSimpleTextBox(String title, String description,
                                         ShepardReactions reaction, TextChannel channel) {
        sendSimpleTextBox(title, description, Color.gray, reaction, channel);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param channel     channel
     */
    public static void sendSimpleTextBox(String title, String description, TextChannel channel) {
        sendSimpleTextBox(title, description, Color.gray, ShepardReactions.NONE, channel);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param channel     channel
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         TextChannel channel) {
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
                                         ShepardReactions reaction, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(TextLocalizer.localizeAll(title, channel))
                .setColor(color)
                .setDescription(TextLocalizer.localizeAll(description, channel));
        if (reaction != ShepardReactions.NONE) {
            builder.setThumbnail(reaction.thumbnail);
        }
        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Sends a error with text box.
     *
     * @param fields  List of fields.
     * @param channel channel
     */
    public static void sendError(LocalizedField[] fields, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        for (LocalizedField field : fields) {
            builder.addField(field.getField())
                    .setColor(Color.red);
            channel.sendMessage(builder.build()).queue();
        }
    }

    /**
     * Sends a simple error with predefined error messages.
     *
     * @param type    error type
     * @param channel channel
     */
    public static void sendSimpleError(ErrorType type, TextChannel channel) {
        if (type.isEmbed) {
            sendSimpleErrorEmbed(localizeAll(type.taggedMessage, channel.getGuild()), channel);
        } else {
            sendMessage(type.taggedMessage, channel);
        }

    }

    /**
     * Sends a simple error to a channel.
     *
     * @param error   Error message
     * @param channel channel
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
     * Sends a simple error to a channel.
     *
     * @param error   Error message
     * @param channel channel
     */
    public static void handlePermissionException(InsufficientPermissionException error, TextChannel channel) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                .setTitle("ERROR!")
                .setDescription(ErrorType.GENERAL.taggedMessage)
                .addField("Error", error.getMessage(), false)
                .setColor(Color.red)
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        try {
            channel.sendMessage(builder.build()).queue();
        } catch (InsufficientPermissionException e) {
            channel.getGuild().getOwner().getUser().openPrivateChannel().queue(a -> {
                EmbedBuilder privateBuilder = new EmbedBuilder()
                        .setTitle("ERROR!")
                        .setDescription("There was an Error on your server **" + channel.getGuild().getName()
                                + "** in Channel **" + channel.getName() + "**, while doing my job.")
                        .addField("Error", error.getMessage(), false)
                        .setColor(Color.red)
                        .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
                a.sendMessage(privateBuilder.build()).queue();
            });
        }
    }

    /**
     * Sends a greeting text.
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

    /**
     * Log a command to the command log channel.
     *
     * @param label          label of command
     * @param args           arguments of command
     * @param messageContext context of command
     */
    public static void logCommand(String label, String[] args, MessageEventDataWrapper messageContext) {
        String command = "```yaml" + lineSeparator()
                + "Executor: " + messageContext.getAuthor().getAsTag() + lineSeparator()
                + "Command:" + messageContext.getMessage().getContentStripped() + lineSeparator()
                + "Guild: " + messageContext.getGuild().getName()
                + " (" + messageContext.getGuild().getId() + ")" + lineSeparator()
                + "```";
        ShepardBot.getLogger().command(command);
        Normandy.getCommandLogChannel().sendMessage(command).queue();
    }

    /**
     * send a simple Message to a channel.
     *
     * @param message Message to send.
     * @param channel channel
     */
    public static void sendMessage(String message, TextChannel channel) {
        if (message.isEmpty() || channel == null) return;

        String localizedMessage = TextLocalizer.localizeAll(message, channel);

        String[] messageParts = localizedMessage.split(lineSeparator());
        StringBuilder messagePart = new StringBuilder();
        for (int i = 0; i < messageParts.length; i++) {
            if (messagePart.length() + messageParts[i].length() < 1024) {
                messagePart.append(messageParts[i]).append(lineSeparator());
            } else {
                channel.sendMessage(messagePart.toString()).queue();
                messagePart = new StringBuilder();
                i--;
            }
        }

        channel.sendMessage(messagePart.toString()).queue();
    }

    /**
     * Sends a message to a channel. No auto localisation is provided.
     *
     * @param message Message to send.
     * @param channel channel to send
     */
    public static void sendMessageToChannel(String message, MessageChannel channel) {
        if (message.isEmpty()) return;

        String[] messageParts = message.split(lineSeparator());
        StringBuilder messagePart = new StringBuilder();
        for (int i = 0; i < messageParts.length; i++) {
            if (messagePart.length() + messageParts[i].length() < 1024) {
                messagePart.append(messageParts[i]).append(lineSeparator());
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
                                      TextChannel messageContext) {
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

    /**
     * Logs a message to message channel if bot is loaded.
     * @param message message to log
     */
    public static void logInfo(String message) {
        if (!ShepardBot.isLoaded()) {
            return;
        }

        MessageSender.sendMessage("```yaml" + lineSeparator() + timestamp() + " | "
                + message + lineSeparator() + "```", Normandy.getLogChannel());
    }
}
