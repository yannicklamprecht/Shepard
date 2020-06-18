package de.eldoria.shepard.DummyClasses;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DummyUser implements User {

    private final JDAImpl jda;

    public DummyUser(JDAImpl jda){
        this.jda = jda;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Test User";
    }

    @Nonnull
    @Override
    public String getDiscriminator() {
        return "0001";
    }

    @Nullable
    @Override
    public String getAvatarId() {
        return null;
    }

    @Nonnull
    @Override
    public String getDefaultAvatarId() {
        return "None";
    }

    @Nonnull
    @Override
    public String getAsTag() {
        return String.format("%#s", this);
    }

    @Override
    public boolean hasPrivateChannel() {
        return false;
    }

    @Nonnull
    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return new RestActionImpl<>(jda, null);
    }

    @Nonnull
    @Override
    public List<Guild> getMutualGuilds() {
        return new ArrayList<>();
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return "@"+getName();
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
