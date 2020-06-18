package de.eldoria.shepard.DummyClasses;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.ChannelManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DummyTextChannel implements TextChannel {

    private final JDAImpl jda;
    private final Guild guild;

    public DummyTextChannel(JDAImpl jda, Guild guild){
        this.jda = jda;
        this.guild = guild;
    }

    @Nullable
    @Override
    public String getTopic() {
        return null;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    @Override
    public int getSlowmode() {
        return 0;
    }

    @Nonnull
    @Override
    public ChannelType getType() {
        return ChannelType.TEXT;
    }

    @Override
    public long getLatestMessageIdLong() {
        return 0;
    }

    @Override
    public boolean hasLatestMessage() {
        return false;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Test Channel";
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @Nullable
    @Override
    public Category getParent() {
        return null;
    }

    @Nonnull
    @Override
    public List<Member> getMembers() {
        return new ArrayList<>();
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public int getPositionRaw() {
        return 0;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Nullable
    @Override
    public PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder) {
        return null;
    }

    @Nonnull
    @Override
    public List<PermissionOverride> getPermissionOverrides() {
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<PermissionOverride> getMemberPermissionOverrides() {
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<PermissionOverride> getRolePermissionOverrides() {
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public ChannelAction<TextChannel> createCopy(@Nonnull Guild guild) {
        return new ChannelActionImpl<>(TextChannel.class, getName(), getGuild(), getType());
    }

    @Nonnull
    @Override
    public ChannelAction<TextChannel> createCopy() {
        return new ChannelActionImpl<>(TextChannel.class, getName(), getGuild(), getType());
    }

    @Nonnull
    @Override
    public ChannelManager getManager() {
        return new ChannelManagerImpl(this);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> delete() {
        return new AuditableRestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public PermissionOverrideAction createPermissionOverride(@Nonnull IPermissionHolder permissionHolder) {
        return new PermissionOverrideActionImpl(jda, this, permissionHolder);
    }

    @Nonnull
    @Override
    public PermissionOverrideAction putPermissionOverride(@Nonnull IPermissionHolder permissionHolder) {
        return new PermissionOverrideActionImpl(jda, this, permissionHolder);
    }

    @Nonnull
    @Override
    public InviteAction createInvite() {
        return new InviteActionImpl(jda, getId());
    }

    @Nonnull
    @Override
    public RestAction<List<Invite>> retrieveInvites() {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public RestAction<List<Webhook>> retrieveWebhooks() {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public WebhookAction createWebhook(@Nonnull String name) {
        return new WebhookActionImpl(jda, this, getName());
    }

    @Nonnull
    @Override
    public RestAction<Void> deleteMessages(@Nonnull Collection<Message> messages) {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public RestAction<Void> deleteMessagesByIds(@Nonnull Collection<String> messageIds) {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> deleteWebhookById(@Nonnull String id) {
        return new AuditableRestActionImpl<>(jda,null);
    }

    @Nonnull
    @Override
    public RestAction<Void> clearReactionsById(@Nonnull String messageId) {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public RestAction<Void> clearReactionsById(@Nonnull String messageId, @Nonnull String unicode) {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public RestAction<Void> clearReactionsById(@Nonnull String messageId, @Nonnull Emote emote) {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public RestAction<Void> removeReactionById(@Nonnull String messageId, @Nonnull String unicode, @Nonnull User user) {
        return new RestActionImpl<>(jda, null);
    }

    @Override
    public boolean canTalk() {
        return false;
    }

    @Override
    public boolean canTalk(@Nonnull Member member) {
        return false;
    }

    @Override
    public int compareTo(@NotNull GuildChannel o) {
        return 0;
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return "#"+getName();
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
