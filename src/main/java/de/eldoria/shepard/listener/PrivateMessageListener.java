package de.eldoria.shepard.listener;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.collections.PrivateMessageCollection;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;

public class PrivateMessageListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent messageContext) {
        User author = messageContext.getAuthor();
        if (author.isBot()) return;
        String receivedMessage = messageContext.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        if (CommandCollection.getInstance().getCommand(args[0].substring(1)) != null) {
            MessageSender.sendMessage(
                    "I'm sorry, but I'm not your personal assistant. I am only available to the public.",
                    messageContext.getChannel());
        } else {
            PrivateMessageCollection.getInstance().addUser(author);
            TextChannel privateAnswerChannel = Normandy.getPrivateAnswerChannel();

            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor(author.getAsTag(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl())
                    .setDescription(messageContext.getMessage().getContentDisplay());
            List<Message.Attachment> attachments = messageContext.getMessage().getAttachments();
            if (!attachments.isEmpty() && attachments.get(0).isImage()) {
                builder.setImage(attachments.get(0).getUrl());
            }
            privateAnswerChannel.sendMessage(builder.build()).queue();
        }
    }
}
