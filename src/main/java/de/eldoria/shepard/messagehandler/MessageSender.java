package de.eldoria.shepard.messagehandler;

import de.eldoria.shepard.C;
import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.util.Replacer;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeByWrapper;
import static java.lang.System.lineSeparator;

@Slf4j
public final class MessageSender {

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param wrapper wrapper for channel determination
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, EventWrapper wrapper) {
        sendTextBox(title, fields, Color.gray, wrapper);
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param wrapper wrapper for channel determination
     * @param color   Color of the text box
     */
    public static void sendTextBox(String title, List<LocalizedField> fields, Color color,
                                   EventWrapper wrapper) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title != null ? TextLocalizer.localizeAll(title, wrapper) : null)
                .setColor(color);
        for (LocalizedField field : fields) {
            builder.addField(field.getField());
        }
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param wrapper     wrapper for channel determination
     */
    public static void sendSimpleTextBox(String title, String description, EventWrapper wrapper) {
        sendSimpleTextBox(title, description, Color.gray, ShepardReactions.NONE, wrapper);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param wrapper     wrapper for channel determination
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         EventWrapper wrapper) {
        sendSimpleTextBox(title, description, color, ShepardReactions.NONE, wrapper);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param reaction    Reaction for thumbnail
     * @param wrapper     wrapper for channel determination
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         ShepardReactions reaction, EventWrapper wrapper) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(TextLocalizer.localizeAll(title, wrapper))
                .setColor(color)
                .setDescription(TextLocalizer.localizeAll(description, wrapper));
        if (reaction != ShepardReactions.NONE) {
            builder.setThumbnail(reaction.thumbnail);
        }
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    /**
     * Sends a error with text box.
     *
     * @param config
     * @param fields  List of fields.
     * @param wrapper wrapper for channel determination
     */
    public static void sendError(Config config, LocalizedField[] fields, EventWrapper wrapper) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("ERROR!")
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        for (LocalizedField field : fields) {
            builder.addField(field.getField())
                    .setColor(Color.red);
            try {
                wrapper.getMessageChannel().sendMessage(builder.build()).queue();
            } catch (InsufficientPermissionException e) {
                handlePermissionException(config, e, wrapper);
            }
        }
    }

    /**
     * Sends a simple error with predefined error messages. Can also replace placeholders in the error message.
     *
     * @param type         error type
     * @param wrapper      channel
     * @param replacements String replacements for localization
     */
    public static void sendSimpleError(ErrorType type, EventWrapper wrapper, Replacement... replacements) {
        if (type.isEmbed && wrapper.getGuild().isPresent()) {
            sendSimpleErrorEmbed(localizeByWrapper(type.taggedMessage, wrapper, replacements), wrapper.getMessageChannel());
        } else if (type.isEmbed) {
            sendSimpleErrorEmbed(localizeByWrapper(type.taggedMessage, wrapper, replacements), wrapper.getMessageChannel());
        } else {
            sendMessage(localizeByWrapper(type.taggedMessage, wrapper, replacements), wrapper.getMessageChannel());
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
            log.error("failed to send error embed", e);
        }
    }

    /**
     * Sends a simple error to a channel.
     *
     * @param config  config of bot
     * @param error   Error message
     * @param wrapper wrapper
     */
    public static void handlePermissionException(Config config, InsufficientPermissionException error,
                                                 EventWrapper wrapper) {
        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle("ERROR!")
                .setDescription(ErrorType.GENERAL.taggedMessage)
                .addField("Error", error.getMessage(), false)
                .setColor(Color.red)
                .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
        try {
            wrapper.getMessageChannel().sendMessage(builder.build()).queue();
        } catch (InsufficientPermissionException e) {
            if (config == null || Arrays.stream(config.getBotlist().getGuildIds())
                    .noneMatch(id -> id == wrapper.getGuild().get().getIdLong())) {
                wrapper.getGuild().get().getOwner().getUser().openPrivateChannel().queue(a -> {
                    EmbedBuilder privateBuilder = new EmbedBuilder()
                            .setTitle("ERROR!")
                            .setDescription("There was an Error on your server **" + wrapper.getGuild().get().getName()
                                    + "** in Channel **" + wrapper.getMessageChannel().getName() + "**, while doing my job.")
                            .addField("Error", error.getMessage(), false)
                            .setColor(Color.red)
                            .setThumbnail(ShepardReactions.CONFUSED.thumbnail);
                    a.sendMessage(privateBuilder.build()).queue();
                });
            }
        }
    }

    /**
     * Sends a greeting text.
     *
     * @param event    event to log
     * @param source   invite source
     * @param greeting Greeting object
     */
    public static void sendGreeting(GuildMemberJoinEvent event, GreetingSettings greeting,
                                    String source) {
        if (greeting.getChannel() == null) return;
        sendGreeting(event.getUser(), greeting, source, greeting.getChannel());
    }

    /**
     * Sends a greeting text.
     *
     * @param channel  channel to log
     * @param source   invite source
     * @param greeting Greeting object
     */
    public static void sendGreeting(User user, GreetingSettings greeting,
                                    String source, @NotNull TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        if (source != null) {
            builder.setFooter("Joined via " + source);
        }
            builder.setTimestamp(Instant.now());
        String message = Replacer.applyUserPlaceholder(user, greeting.getMessage());
        builder.addField(user.getAsTag(),
                message, true)
                .setColor(Color.green)
                .setThumbnail(user.getAvatarUrl() == null
                        ? ShepardReactions.EXCITED.thumbnail
                        : user.getAvatarUrl());
        channel.sendMessage(builder.build()).queue();
    }


    /**
     * Log a command to the command log channel.
     *
     * @param label   label of command
     * @param args    arguments of command
     * @param wrapper context of command
     */
    public static void logCommand(String label, String[] args, EventWrapper wrapper) {
        var mention = wrapper.getAuthor().getAsTag();
        var cmd = wrapper.getMessage().get().getContentStripped();
        var guild = "Private message";
        var guildId = "0";

        if (wrapper.isGuildEvent()) {
            guild = wrapper.getGuild().get().getName();
            guildId = wrapper.getGuild().get().getId();
            log.debug(C.COMMAND, "command execution by {} in guild {}({}): {}", mention, guild, guildId, cmd);
        } else {
            log.debug(C.COMMAND, "command execution by {} in private message: {}", mention, cmd);
        }

    }

    /**
     * send a simple Message to a channel.
     *
     * @param message Message to send.
     * @param channel channel
     */
    public static void sendMessage(String message, MessageChannel channel) {
        if (message.isEmpty() || channel == null) return;

        String localizedMessage = TextLocalizer.localizeAllByChannel(message, channel);

        sendSplitMessage(localizedMessage, channel);
    }

    /**
     * Sends a message to a channel. No auto localisation is provided.
     *
     * @param message Message to send.
     * @param channel channel to send
     */
    public static void sendMessageToChannel(String message, MessageChannel channel) {
        if (message.isEmpty()) return;

        sendSplitMessage(message, channel);
    }

    private static void sendSplitMessage(String message, MessageChannel channel) {
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
                                      MessageChannel messageContext) {
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

    public static void sendLocalized(String message, EventWrapper wrapper, Replacement... replacements) {
        wrapper.getMessageChannel().sendMessage(TextLocalizer.localizeByWrapper(message, wrapper, replacements)).queue();
    }
}
