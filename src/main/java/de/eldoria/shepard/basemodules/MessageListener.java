package de.eldoria.shepard.basemodules;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqMessageTrigger;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter implements ReqDataSource {

    private DataSource source;
    private final List<ReqMessageTrigger> triggerList = new ArrayList<>();

    private final Map<UserMessageCacheKey, Cache<Long, CachedMessage>> latestUserMessages = new HashMap<>();
    private final Map<ChannelMessageCacheKey, Cache<Long, CachedMessage>> latestChannelMessages = new HashMap<>();

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
    }

    @Override
    public void onPrivateMessageUpdate(@Nonnull PrivateMessageUpdateEvent event) {
        super.onPrivateMessageUpdate(event);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        super.onGuildMessageUpdate(event);
    }

    @Override
    public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event) {

    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    private void saveLatestMessage(EventWrapper wrapper) {
        if (!wrapper.isGuildEvent()) return;
        UserMessageCacheKey key = new UserMessageCacheKey(
                wrapper.getGuild().get().getIdLong(),
                wrapper.getMessageChannel().getIdLong(),
                wrapper.getActor().getIdLong());
        latestChannelMessages.computeIfAbsent(key, k -> getNewMessageCache(100));
        latestUserMessages.computeIfAbsent(key, k -> getNewMessageCache(20));
    }

    private Cache<Long, CachedMessage> getNewMessageCache(int size) {
        return CacheBuilder.newBuilder().maximumSize(size).expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    public void registerTrigger(ReqMessageTrigger trigger) {
        triggerList.add(trigger);
    }

    @Getter
    @RequiredArgsConstructor
    private static class CachedMessage {
        private final long userId;
        private final String contentRaw;
        private final Instant timeSend;

        public CachedMessage(long userId, String contentRaw) {
            this.userId = userId;
            this.contentRaw = contentRaw;
            timeSend = Instant.now();
        }
    }

    private static class UserMessageCacheKey extends ChannelMessageCacheKey {
        private final long userId;

        public UserMessageCacheKey(long guildId, long channelId, long userId) {
            super(guildId, channelId);
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UserMessageCacheKey that = (UserMessageCacheKey) o;
            return guildId == that.guildId &&
                    channelId == that.channelId &&
                    userId == that.userId;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), guildId, channelId, userId);
        }
    }

    @RequiredArgsConstructor
    private static class ChannelMessageCacheKey {
        protected final long guildId;
        protected final long channelId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChannelMessageCacheKey that = (ChannelMessageCacheKey) o;
            return guildId == that.guildId &&
                    channelId == that.channelId;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(guildId, channelId);
        }
    }
}
