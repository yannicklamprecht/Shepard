package de.eldoria.shepard.basemodules.commanddispatching.dialogue;

import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class DialogHandler {
    // Guild -> Channel -> User -> Dialog
    private final Map<Long, Map<Long, Map<Long, Dialog>>> dialogs = new HashMap<>();

    /**
     * Invoke a dialog in this context.
     *
     * @param wrapper   message event
     * @param message message of member
     * @return true if a dialog was invoked.
     */
    public boolean invoke(EventWrapper wrapper, Message message) {
        String content = message.getContentRaw();
        ISnowflake guild = wrapper.getGuild().isPresent() ? wrapper.getGuild().get() : FAKE;
        MessageChannel channel = wrapper.getMessageChannel();
        User actor = wrapper.getActor();
        if ("exit".equalsIgnoreCase(content) || "cancel".equalsIgnoreCase(content)) {
            if (removeDialog(guild, channel, actor)) {
                MessageSender.sendLocalized("dialog.canceled", wrapper);
                return true;
            }
            return false;
        }

        Dialog dialog = getDialog(wrapper);
        if (dialog != null) {
            boolean skip = TextLocalizer.localizeByWrapper("dialog.skip", wrapper).equalsIgnoreCase(content);
            boolean remove = TextLocalizer.localizeByWrapper("dialog.remove", wrapper).equalsIgnoreCase(content);

            if (dialog.invoke(wrapper, message, skip, remove)) {
                removeDialog(guild, channel, actor);
                MessageSender.sendLocalized("dialog.done", wrapper);
            }
            return true;
        }
        return false;
    }

    public boolean dialogInProgress(EventWrapper wrapper) {
        return getDialog(wrapper) != null;
    }

    public boolean removeDialog(ISnowflake guild, ISnowflake channel, ISnowflake member) {
        var guildDialogs = dialogs.get(guild.getIdLong());
        if (guildDialogs == null) return false;
        var channelDialogs = guildDialogs.get(channel.getIdLong());
        if (channelDialogs == null) return false;
        Dialog dialog = channelDialogs.get(member.getIdLong());
        if (dialog == null) return false;

        channelDialogs.remove(member.getIdLong());

        if (channelDialogs.isEmpty()) {
            guildDialogs.remove(channel.getIdLong());
        }

        if (guildDialogs.isEmpty()) {
            dialogs.remove(guild.getIdLong());
        }
        return true;
    }

    public void startDialog(EventWrapper event, String startMessage, Dialog dialog) {
        ISnowflake guild = event.getGuild().isPresent() ? event.getGuild().get() : FAKE;
        MessageChannel channel = event.getMessageChannel();
        User actor = event.getActor();

        if (dialogInProgress(event)) {
            MessageSender.sendLocalized("dialog.inProgress", event);
            return;
        }

        MessageSender.sendLocalized(startMessage, event);

        dialogs.computeIfAbsent(guild.getIdLong(), k -> new HashMap<>())
                .computeIfAbsent(channel.getIdLong(), k -> new HashMap<>())
                .put(actor.getIdLong(), dialog);
    }

    public Dialog getDialog(EventWrapper event) {
        ISnowflake guild = event.getGuild().isPresent() ? event.getGuild().get() : FAKE;
        MessageChannel channel = event.getMessageChannel();
        User actor = event.getActor();

        var guildDialogs = dialogs.get(guild.getIdLong());
        if (guildDialogs == null) return null;
        var channelDialogs = guildDialogs.get(channel.getIdLong());
        if (channelDialogs == null) return null;
        return channelDialogs.get(actor.getIdLong());
    }

    private static final ISnowflake FAKE = () -> 0;
}
