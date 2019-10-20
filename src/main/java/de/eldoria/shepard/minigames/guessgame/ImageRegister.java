package de.eldoria.shepard.minigames.guessgame;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ImageRegister {
    private static ImageRegister instance;

    private final Map<UserChannelKey, ImageConfiguration> configurations = new HashMap<>();

    private ImageRegister() {
    }

    public static ImageRegister getInstance() {
        if (instance == null) {
            instance = new ImageRegister();
        }
        return instance;
    }

    public void startConfiguration(MessageEventDataWrapper messageContext, boolean hentai) {
        configurations.put(new UserChannelKey(messageContext), new ImageConfiguration(hentai));
    }

    public void addImage(MessageEventDataWrapper messageContext, String url) {
        switch (getConfigurationState(messageContext)) {
            case NONE:
            case CONFIGURED:
                break;
            case CROPPED:
            case FULL:
                configurations.get(new UserChannelKey(messageContext)).addImage(url);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + getConfigurationState(messageContext));
        }
    }

    public boolean registerConfiguration(MessageEventDataWrapper messageContext) {
        if (getConfigurationState(messageContext) == ConfigurationType.CONFIGURED) {
            UserChannelKey channelKey = new UserChannelKey(messageContext);
            boolean success = configurations.get(channelKey).registerAtDatabase();
            configurations.remove(channelKey);
            return success;

        }
        return false;
    }

    public void cancelConfiguration(MessageEventDataWrapper messageContext) {
        configurations.remove(new UserChannelKey(messageContext));
    }

    public ConfigurationType getConfigurationState(MessageEventDataWrapper messageContext) {
        UserChannelKey key = new UserChannelKey(messageContext);
        if (configurations.containsKey(key)) {
            return configurations.get(key).getConfigurationState();
        }
        return ConfigurationType.NONE;
    }

    public ImageConfiguration getConfiguration(MessageEventDataWrapper messageContext) {
        return configurations.get(new UserChannelKey(messageContext));
    }

    private static class UserChannelKey {
        final long userId;
        final long channelId;

        UserChannelKey(MessageEventDataWrapper messageContext) {
            this.userId = messageContext.getAuthor().getIdLong();
            this.channelId = messageContext.getChannel().getIdLong();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserChannelKey that = (UserChannelKey) o;
            return Objects.equals(userId, that.userId) && Objects.equals(channelId, that.channelId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, channelId);
        }
    }
}
