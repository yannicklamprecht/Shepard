package de.eldoria.shepard.DummyClasses;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildVoiceStateImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class DummyMember implements Member {

    private final User user;
    private final Guild guild;
    private final JDAImpl jda;

    public DummyMember(JDAImpl jda){
        user = new DummyUser(jda);
        guild = new DummyGuild(jda);
        this.jda = jda;
    }
    @Nonnull
    @Override
    public User getUser() {
        return user;
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissions() {
        return EnumSet.allOf(Permission.class);
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
        return EnumSet.allOf(Permission.class);
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissionsExplicit() {
        return EnumSet.allOf(Permission.class);
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
        return EnumSet.allOf(Permission.class);
    }

    @Override
    public boolean hasPermission(@Nonnull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@Nonnull Collection<Permission> permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions) {
        return false;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeJoined() {
        return OffsetDateTime.now();
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeBoosted() {
        return OffsetDateTime.now();
    }

    @Nullable
    @Override
    public GuildVoiceState getVoiceState() {
        return new GuildVoiceStateImpl(this);
    }

    @Nonnull
    @Override
    public List<Activity> getActivities() {
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public OnlineStatus getOnlineStatus() {
        return OnlineStatus.UNKNOWN;
    }

    @Nonnull
    @Override
    public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
        return OnlineStatus.UNKNOWN;
    }

    @Nonnull
    @Override
    public EnumSet<ClientType> getActiveClients() {
        return EnumSet.allOf(ClientType.class);
    }

    @Nullable
    @Override
    public String getNickname() {
        return null;
    }

    @Nonnull
    @Override
    public String getEffectiveName() {
        return "Test User Effective";
    }

    @Nonnull
    @Override
    public List<Role> getRoles() {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public int getColorRaw() {
        return 0;
    }

    @Override
    public boolean canInteract(@Nonnull Member member) {
        return false;
    }

    @Override
    public boolean canInteract(@Nonnull Role role) {
        return false;
    }

    @Override
    public boolean canInteract(@Nonnull Emote emote) {
        return false;
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return "Test User As Mention";
    }

    @Override
    public long getIdLong() {
        return getUser().getIdLong();
    }
}
