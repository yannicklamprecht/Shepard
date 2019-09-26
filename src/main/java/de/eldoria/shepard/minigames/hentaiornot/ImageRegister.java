package de.eldoria.shepard.minigames.hentaiornot;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public final class ImageRegister {
    private static ImageRegister instance;

    private Map<String, ImageConfiguration> configurations = new HashMap<>();

    private ImageRegister() {
    }

    public static ImageRegister getInstance() {
        if (instance == null) {
            instance = new ImageRegister();
        }
        return instance;
    }

    public void startConfiguration(User user, boolean hentai) {
        configurations.put(user.getId(), new ImageConfiguration(hentai));
    }

    public void addImage(User user, String url) {
        switch (getConfigurationState(user)) {
            case NONE:
                break;
            case CROPPED:
            case FULL:
                configurations.get(user.getId()).addImage(url);
                break;
            case CONFIGURED:
                break;
        }
    }

    public boolean registerConfiguration(User user) {
        if (getConfigurationState(user) == ConfigurationType.CONFIGURED) {
            boolean success = configurations.get(user.getId()).registerAtDatabase();
            configurations.remove(user.getId());
            return success;

        }
        return false;
    }

    public void cancelConfiguration(User user) {
        configurations.remove(user.getId());
    }

    public ConfigurationType getConfigurationState(User user) {
        if (configurations.containsKey(user.getId())) {
            return configurations.get(user.getId()).getConfigurationState();
        }
        return ConfigurationType.NONE;
    }

    public ImageConfiguration getConfiguration(User user) {
        return configurations.getOrDefault(user.getId(), null);
    }
}
