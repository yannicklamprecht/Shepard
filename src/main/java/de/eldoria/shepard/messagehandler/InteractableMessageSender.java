package de.eldoria.shepard.messagehandler;

import de.eldoria.shepard.collections.ReactionActionCollection;
import de.eldoria.shepard.reactionactions.Action;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public final class InteractableMessageSender {
    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel to send.
     * @param actions one ore more reaction actions linked with this message.
     */
    public static void sendTextBox(String title, List<MessageEmbed.Field> fields, TextChannel channel,
                                   Action... actions) {
        sendTextBox(title, fields, channel, Color.gray, actions);
    }

    /**
     * Sends a textbox to a channel.
     *
     * @param title   Title of the chatbox.
     * @param fields  List of fields for the chatbox.
     * @param channel channel to send.
     * @param color   Color of the text box
     * @param actions one ore more reaction actions linked with this message.
     */
    public static void sendTextBox(String title, List<MessageEmbed.Field> fields, TextChannel channel, Color color,
                                   Action... actions) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .setColor(color);
        for (MessageEmbed.Field field : fields) {
            builder.addField(field);
        }
        channel.sendMessage(builder.build()).queue(message -> registerActions(channel, message, actions));
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param reaction    Reaction for thumbnail
     * @param channel     channel to send
     * @param actions     one ore more reaction actions linked with this message.
     */
    public static void sendSimpleTextBox(String title, String description,
                                         ShepardReactions reaction, TextChannel channel,
                                         Action... actions) {
        sendSimpleTextBox(title, description, Color.gray, reaction, channel, actions);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param channel     channel to send
     * @param actions     one ore more reaction actions linked with this message.
     */
    public static void sendSimpleTextBox(String title, String description, TextChannel channel,
                                         Action... actions) {
        sendSimpleTextBox(title, description, Color.gray, ShepardReactions.NONE, channel, actions);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param channel     channel to send
     * @param actions     one ore more reaction actions linked with this message.
     */
    public static void sendSimpleTextBox(String title, String description, Color color, TextChannel channel,
                                         Action... actions) {
        sendSimpleTextBox(title, description, color, ShepardReactions.NONE, channel, actions);
    }

    /**
     * Send a simple text box with title and text.
     *
     * @param title       Title of text box
     * @param description Text of textbox
     * @param color       Color of the text box
     * @param reaction    Reaction for thumbnail
     * @param channel     channel to send
     * @param actions     one ore more reaction actions linked with this message.
     */
    public static void sendSimpleTextBox(String title, String description, Color color,
                                         ShepardReactions reaction, TextChannel channel,
                                         Action... actions) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setDescription(description);
        if (reaction != ShepardReactions.NONE) {
            builder.setThumbnail(reaction.thumbnail);
        }
        channel.sendMessage(builder.build()).queue(message -> registerActions(channel, message, actions));
    }

    private static void registerActions(TextChannel channel, Message message, Action... actions) {
        Arrays.stream(actions).forEach(action -> {
            ReactionActionCollection.getInstance().addReactionAction(channel, message, action);
            message.addReaction(action.getReaction()).queue();
        });
    }
}
