package de.eldoria.shepard.configuration;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to load the config from a specific path.
 */
@Slf4j
public final class Loader {
    private Loader() { }

    public static Config loadConfig() throws FileNotFoundException {
        File shepardJar = new File(".");
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();

        Path configFile = Paths.get(home + "/" + System.getProperty("shepard.config"));

        InputStream inputStream;
        Yaml yaml = new Yaml(new Constructor(Config.class));

        inputStream = new FileInputStream(configFile.toString());

        log.info("Config loaded");
        return yaml.load(inputStream);
    }
}
