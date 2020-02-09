package de.eldoria.shepard.configuration;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to load the config from a specific path.
 */
@Slf4j
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

        Path configDir = Paths.get(home, "/config");

        if (!Files.exists(configDir)) {
			log.info("{} not found. Trying to create", configDir);
            try {
                Files.createDirectory(configDir);
            } catch (IOException e) {
				log.error("Directory for config could not be created!", e);
                return;
            }
        } else {
			log.info("Config directory found!");
        }

        Path configFile = Paths.get(configDir + "/config.yml");

        if (!Files.exists(configFile)) {
			log.info("Config file not found! Trying to create a default config!");

            try {
                Files.createFile(configFile);
            } catch (IOException e) {
				log.error("Could not create config file", e);
            }
            InputStream systemResource = getClass().getClassLoader().getResourceAsStream("config.yml");
            if (systemResource == null) {
                return;
            }
	
			log.info("Loading config template!");

            OutputStream os = null;
            try {
                os = new FileOutputStream(configFile.toFile());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = systemResource.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

            } catch (IOException e) {
				log.error("failed to read config file", e);
            } finally {
                try {
                    systemResource.close();
                    if (os != null) {
                        os.close();
                    }

                } catch (IOException e) {
					log.error("", e);
                }
            }

            log.info("Config file created!");
        }

        InputStream inputStream;
        Yaml yaml = new Yaml(new Constructor(Config.class));
        try {
            inputStream = new FileInputStream(configFile.toString());
        } catch (FileNotFoundException e) {
            log.error("File not found!", e);
            return;
        }
        try {
            this.config = yaml.load(inputStream);
        } catch (RuntimeException e) {
            log.error("failed to load yml config", e);
            return;
        }
        log.info("Config loaded");
    }
}
