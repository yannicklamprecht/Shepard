package de.eldoria.shepard.commandmodules.privatemessages.listener;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.privatemessages.PrivateMessageCollection;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqPrivateMessages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;

public class PrivateMessageListener extends ListenerAdapter implements ReqCommands, ReqPrivateMessages, ReqNormandy {

    private CommandHub commands;
    private PrivateMessageCollection privateMessages;
    private Normandy normandy;

    /**
     * Create a new private message listener.
     */
    public PrivateMessageListener() {
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent messageContext) {
        User author = messageContext.getAuthor();
        if (author.isBot()) return;
        String receivedMessage = messageContext.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        if (!args[0].isEmpty() && commands.getCommand(args[0].substring(1)).isPresent()) {
            MessageSender.sendMessageToChannel(
                    "I'm sorry, but I'm not your personal assistant. I am only available to the public.",
                    messageContext.getChannel());
        } else {
            privateMessages.addUser(author);
            TextChannel privateAnswerChannel = normandy.getPrivateAnswerChannel();

            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor(author.getAsTag(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl());
            if (!messageContext.getMessage().getContentDisplay().isEmpty()) {
                builder.setDescription(messageContext.getMessage().getContentDisplay());

            }
            List<Message.Attachment> attachments = messageContext.getMessage().getAttachments();
            if (!attachments.isEmpty() && attachments.get(0).isImage()) {
                builder.setImage(attachments.get(0).getUrl());
            }
            privateAnswerChannel.sendMessage(builder.build()).queue();
        }
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commands = commandHub;
    }

    @Override
    public void addPrivateMessages(PrivateMessageCollection privateMessages) {
        this.privateMessages = privateMessages;
    }

    @Override
    public void addNormandy(Normandy normandy) {
        this.normandy = normandy;
    }
}
