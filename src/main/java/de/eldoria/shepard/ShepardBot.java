package de.eldoria.shepard;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.configuration.Config;
import de.eldoria.shepard.configuration.Loader;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.register.ContextRegister;
import de.eldoria.shepard.register.ListenerRegister;
import de.eldoria.shepard.util.ExitCode;
import de.eldoria.shepard.webapi.ApiHandler;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.util.List;

@Slf4j
public final class ShepardBot {
    private static JDA jda;
    private static Config config;
    private static ShepardBot instance;

    private boolean loaded;
	
	private ShepardBot() {
		log.info(C.STATUS, "Startup in progress. Bot is heating up");
		config = Loader.getConfigLoader().getConfig();
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
    	try {
			instance = new ShepardBot();
		
		
			instance.setup();
		
			ApiHandler.getInstance();
		
			instance.loaded = true;
		} catch (Exception e) {
    		log.error("failed to start bot", e);
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

    private void setup() {
        try {
            initiateJda();
        } catch (LoginException | InterruptedException e) {
            log.error(C.NOTIFY_ADMIN, "jda failed to log in", e);
        }

        ContextRegister.registerContexts();
        ListenerRegister.registerListener();
		log.info("Registered {} Commands", CommandCollection.getInstance().getCommands().size());
		log.info("Registered {} Keywords", KeyWordCollection.getInstance().getKeywords().size());
		log.info("Registered on {} Guilds!", jda.getGuilds().size());

        if (config.debugActive()) {
            CommandCollection.getInstance().debug();
            KeyWordCollection.getInstance().debug();
        }

        MessageSender.sendSimpleTextBox("Shepard meldet sich zum Dienst! Erwarte ihre Befehle!",
                "Registered " + CommandCollection.getInstance().getCommands().size() + " Commands!"
                        + System.lineSeparator()
                        + "Registered " + KeyWordCollection.getInstance().getKeywords().size() + " Keywords!"
                        + System.lineSeparator()
                        + "Serving " + jda.getGuilds().size() + " Guilds!",
                Color.GREEN, ShepardReactions.EXCITED, Normandy.getGeneralLogChannel());


        log.info("Setup complete!");
    }

    private void initiateJda() throws LoginException, InterruptedException {
        jda = new JDABuilder(config.getToken()).setMaxReconnectDelay(60).build();

        // optionally block until JDA is ready
        jda.awaitReady();

        log.info(C.STATUS, "JDA initialized");
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
     *
     * @param exitCode exit code do determine what should happen after shutdown
     *                 0 = shutdown
     *                 10 = restart
     */
    public void shutdown(ExitCode exitCode) {
        loaded = false;
        if (exitCode == ExitCode.SHUTDOWN) {
            MessageSender.sendSimpleTextBox("Shutdown.",
                    "",
                    Color.RED, ShepardReactions.ASLEEP, Normandy.getGeneralLogChannel());
        }
        if (exitCode == ExitCode.RESTART) {
            MessageSender.sendSimpleTextBox("Restarting",
                    "",
                    new Color(17, 209, 209), ShepardReactions.WINK, Normandy.getGeneralLogChannel());
        }

        if (jda != null) {
            jda.shutdown();
            log.info("JDA shut down. Closing Application in 2 Seconds!");
        }
        jda = null;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.info("Shutdown interrupted!");
        }

        System.exit(exitCode.code);

    }

    /**
     * Checks if the bot is fully loaded.
     *
     * @return true if the bot instance is not null and {@link ShepardBot#loaded} is true.
     * False if the bot is starting or going to shut down.
     */
    public static boolean isLoaded() {
        if (instance == null) {
            return false;
        }

        return instance.loaded;
    }
}
