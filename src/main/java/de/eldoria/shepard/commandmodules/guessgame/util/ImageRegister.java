package de.eldoria.shepard.commandmodules.guessgame.util;

import de.eldoria.shepard.commandmodules.guessgame.data.GuessGameData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ImageRegister implements ReqDataSource {
    private final Map<UserChannelKey, ImageConfiguration> configurations = new HashMap<>();
    private GuessGameData guessGameData;

    /**
     * Create a new image register.
     */
    public ImageRegister() {
    }

    /**
     * Starts a new configruation for a user.
     *
     * @param wrapper message context
     * @param nsfw           true if nsfw
     */
    public void startConfiguration(EventWrapper wrapper, boolean nsfw) {
        configurations.put(new UserChannelKey(wrapper), new ImageConfiguration(nsfw));
    }

    /**
     * Add a new image.
     *
     * @param wrapper message context
     * @param url            url to add
     */
    public void addImage(EventWrapper wrapper, String url) {
        switch (getConfigurationState(wrapper)) {
            case NONE:
            case CONFIGURED:
                break;
            case CROPPED:
            case FULL:
                configurations.get(new UserChannelKey(wrapper)).addImage(url);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + getConfigurationState(wrapper));
        }
    }

    /**
     * Register configuration if registration is complete.
     *
     * @param wrapper message context
     * @return true if the image was registered
     */
    public boolean registerConfiguration(EventWrapper wrapper) {
        if (getConfigurationState(wrapper) == ConfigurationState.CONFIGURED) {
            UserChannelKey channelKey = new UserChannelKey(wrapper);
            boolean success = configurations.get(channelKey).registerAtDatabase(guessGameData);
            configurations.remove(channelKey);
            return success;

        }
        return false;
    }

    /**
     * Cancel a registration of a image.
     *
     * @param messageContext message context
     */
    public void cancelConfiguration(EventWrapper messageContext) {
        configurations.remove(new UserChannelKey(messageContext));
    }

    /**
     * Get the current configuration state for a user in a channel.
     *
     * @param wrapper message context
     * @return configuration state
     */
    public ConfigurationState getConfigurationState(EventWrapper wrapper) {
        UserChannelKey key = new UserChannelKey(wrapper);
        if (configurations.containsKey(key)) {
            return configurations.get(key).getConfigurationState();
        }
        return ConfigurationState.NONE;
    }

    /**
     * Get the current image configuration.
     *
     * @param wrapper message context
     * @return configuration or null
     */
    public ImageConfiguration getConfiguration(EventWrapper wrapper) {
        return configurations.get(new UserChannelKey(wrapper));
    }

    @Override
    public void addDataSource(DataSource source) {
        guessGameData = new GuessGameData(source);
    }

    private static class UserChannelKey {
        final long userId;
        final long channelId;

        UserChannelKey(EventWrapper wrapper) {
            this.userId = wrapper.getAuthor().getIdLong();
            this.channelId = wrapper.getMessageChannel().getIdLong();
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
