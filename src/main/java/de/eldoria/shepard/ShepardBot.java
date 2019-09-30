package de.eldoria.shepard;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.configuration.Config;
import de.eldoria.shepard.configuration.Loader;
import de.eldoria.shepard.io.ConsoleReader;
import de.eldoria.shepard.io.Logger;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.register.ContextRegister;
import de.eldoria.shepard.register.ListenerRegister;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.util.List;

public final class ShepardBot {
    private static JDA jda;
    private static Config config;
    private static ShepardBot instance;
    private static Logger logger;

    private ShepardBot() {
        try {
            System.out.println("Startup in progress. Bot is heating up");
            System.out.println("Initialising Logger");
            logger = new Logger();
            config = Loader.getConfigLoader().getConfig();
            Thread.sleep(100);
            ConsoleReader.initialize();
            logger.info("Console initialized");

            logger.info("Initialising JDA");

            // Note: It is important to register your ReadyListener before building
            if (config.debugActive()) {
                org.apache.log4j.BasicConfigurator.configure();
            }

        } catch (InterruptedException e) {
            System.out.println("Startup interrupted");
        }

    }

    private void setup() {
        ContextRegister.registerContexts();
        ListenerRegister.registerListener();
        logger.info("Registered " + CommandCollection.getInstance().getCommands().size() + " Commands");
        logger.info("Registered " + KeyWordCollection.getInstance().getKeywords().size() + " Keywords");
        logger.info("Registered on " + jda.getGuilds().size() + " Guilds!");

        if (config.debugActive()) {
            CommandCollection.getInstance().debug();
            KeyWordCollection.getInstance().debug();
        }

        try {
            initiateJda();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        MessageSender.sendSimpleTextBox("Shepard meldet sich zum Dienst! Erwarte ihre Befehle!",
                "Registered " + CommandCollection.getInstance().getCommands().size() + " Commands!"
                        + System.lineSeparator()
                        + "Registered " + KeyWordCollection.getInstance().getKeywords().size() + " Keywords!"
                        + System.lineSeparator()
                        + "Serving " + jda.getGuilds().size() + " Guilds!",
                Color.GREEN, ShepardReactions.EXCITED, Normandy.getGeneralLogChannel());

        logger.info("Setup complete!");
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
        instance = new ShepardBot();


        instance.setup();

    }

    private void initiateJda() throws LoginException, InterruptedException {
        jda = new JDABuilder(config.getToken()).setMaxReconnectDelay(60).build();

        // optionally block until JDA is ready
        jda.awaitReady();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            ShepardBot.getLogger().error(e.getMessage());
        }

        logger.info("JDA initialized");
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
        MessageSender.sendSimpleTextBox("Shepard verlässt die Brücke!!",
                "",
                Color.RED, ShepardReactions.ASLEEP, Normandy.getGeneralLogChannel());

        if (jda != null) {
            jda.shutdown();
            ShepardBot.getLogger().info("JDA shut down. Closing Application in 5 Seconds!");
        }
        jda = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            ShepardBot.getLogger().info("Shutdown interrupted!");
        }

        System.exit(0);
    }

    public static Logger getLogger() {
        return logger;
    }
}
