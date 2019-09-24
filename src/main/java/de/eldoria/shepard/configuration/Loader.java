package de.eldoria.shepard.configuration;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.io.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static java.lang.System.out;

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
        File shepardJar = new File(".");
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();

        Logger logger = ShepardBot.getLogger();

        Path configDir = Paths.get(home, "/config");

        if (!Files.exists(configDir)) {
            logger.info(configDir + " not found. Trying to create");
            try {
                Files.createDirectory(configDir);
            } catch (IOException e) {
                logger.error("Directory for config could not be created!", e);
                return;
            }
        } else {
            logger.info("Config directory found!");
        }

        Path configFile = Paths.get(configDir + "/config.yml");

        if (!Files.exists(configFile)) {
            logger.info("Config file not found! Trying to create a default config!");

            try {
                Files.createFile(configFile);
            } catch (IOException e) {
                logger.error("Could not create config file", e);
            }
            InputStream systemResource;
            try {
                systemResource = getClass().getClassLoader().getResourceAsStream("config.yml");
                out.println(Objects.requireNonNull(systemResource).toString());
            } catch (NullPointerException e) {
                logger.error(e);
                return;
            }

            logger.info("Loading config template!");

            InputStream is = null;
            OutputStream os = null;
            try {
                is = systemResource;
                os = new FileOutputStream(configFile.toFile());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

            } catch (IOException e) {
                logger.error(e);
            } finally {
                try {
                    is.close();
                    Objects.requireNonNull(os).close();

                } catch (NullPointerException | IOException e) {
                    logger.error(e);
                    return;
                }
            }

            logger.info("Config file created!");


        }

        InputStream inputStream;
        Yaml yaml = new Yaml(new Constructor(Config.class));
        try {
            inputStream = new FileInputStream(configFile.toString());
        } catch (FileNotFoundException e) {
            logger.error("File not found!", e);
            return;
        }
        this.config = yaml.load(inputStream);
        logger.info("Config loaded");
    }
}
