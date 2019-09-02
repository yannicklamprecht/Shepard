package de.eldoria.shepard;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.configuration.Config;
import de.eldoria.shepard.configuration.Loader;
import de.eldoria.shepard.io.ConsoleReader;
import de.eldoria.shepard.io.Logger;
import de.eldoria.shepard.register.ContextRegister;
import de.eldoria.shepard.register.ListenerRegister;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;

public final class ShepardBot {
    private static JDA jda;
    private static Config config;
    private static ShepardBot instance;
    private static Logger logger;

    private ShepardBot() {
        config = Loader.getConfigLoader().getConfig();

        try {
            initiateJda();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        logger = new Logger();
        new ConsoleReader();

        ContextRegister.registerContexts();
        ListenerRegister.registerListener();

        CommandCollection.getInstance().debug();
        KeyWordCollection.getInstance().debug();

    }

    /**
     * Returns the Shepard Bot instance.
     *
     * @return Instance of Shepard bot.
     */
    public static ShepardBot getInstance() {
        return instance;
    }

    /**
     * Main method.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        // Note: It is important to register your ReadyListener before building
        org.apache.log4j.BasicConfigurator.configure();

        instance = new ShepardBot();

        instance.setup();

    }

    private void initiateJda() throws LoginException, InterruptedException {
        jda = new JDABuilder(config.getToken()).build();

        // optionally block until JDA is ready
        jda.awaitReady();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets the jda.
     *
     * @return JDA object
     */
    public static JDA getJDA() {
        return jda;
    }

    /**
     * Get the config.
     *
     * @return Config object
     */
    public static Config getConfig() {
        return config;
    }

    /**
     * Registers listener at jda.
     *
     * @param listener List of listener.
     */
    public void registerListener(List<ListenerAdapter> listener) {
        for (ListenerAdapter l : listener) {
            jda.addEventListener(l);
        }
    }

    /**
     * Close the shepard application.
     */
    public void shutdown() {
        jda.shutdown();
        System.out.println("JDA shut down. Closing Application in 5 Seconds!");
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e){
            System.out.println("Shutdown interrupted!");
        }

        System.exit(0);
    }

    public static Logger getLogger() {
        return logger;
    }
}
