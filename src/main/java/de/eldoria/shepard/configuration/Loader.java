package de.eldoria.shepard.configuration;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.io.Logger;
import de.eldoria.shepard.messagehandler.MessageSender;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public final class Loader {
    private static Loader loader;

    private Config config;

    private Loader() {
        reloadConfig();
    }

    /**
     * Get config loader instance.
     *
     * @return return Loader instance
     */
    public static Loader getConfigLoader() {
        if (loader == null) {
            loader = new Loader();
        }
        return loader;
    }

    /**
     * Get config.
     *
     * @return config object
     */
    public Config getConfig() {
        return config;
    }

    private void reloadConfig() {
        File shepardJar = new File(ClassLoader.getSystemClassLoader()
                .getResource(".").getPath());
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();

        Path configDir = Paths.get(home + "/config");

        if (!Files.exists(configDir)) {
            try {
                Files.createDirectory(configDir);
            } catch (IOException e) {
                ShepardBot.getLogger().error("Directory for config could not be created!"
                        + lineSeparator() + getStackTrace(e));
            }
        }

        Path configFile = Paths.get(configDir + "\\config.yml");

        if (!Files.exists(configFile)) {
            try {
                Files.copy(Paths.get(Paths.get(getClass().getClassLoader().getResource("config.yml")
                                .toURI()).toFile().getAbsolutePath()),
                        configFile);
            } catch (IOException | URISyntaxException e) {
                ShepardBot.getLogger().error("Config file could not be created!"
                        + lineSeparator() + getStackTrace(e));
            }
        }

        InputStream inputStream;
        Yaml yaml = new Yaml(new Constructor(Config.class));
        try {
            inputStream = new FileInputStream(configFile.toString());
        } catch (FileNotFoundException e) {
            ShepardBot.getLogger().error("File not found!"
                    + lineSeparator() + getStackTrace(e));
            return;
        }
        this.config = yaml.load(inputStream);
    }
}
