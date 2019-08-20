package de.chojo.shepard.configuration;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public final class Loader {
    private static Loader loader;

    private Config config;

    private Loader(){
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.yml");
        Config config = yaml.load(inputStream);
        this.config = config;
        Database d = config.getDatabase();
    }

    public static Loader getConfigLoader(){
        if(loader == null){
            loader = new Loader();
        }
        return loader;
    }

    public Config getConfig() {
        return config;
    }
}
