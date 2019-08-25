package de.eldoria.shepard;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.configuration.Config;
import de.eldoria.shepard.configuration.Loader;
import de.eldoria.shepard.contexts.commands.admin.Prefix;
import de.eldoria.shepard.contexts.commands.admin.Invite;
import de.eldoria.shepard.contexts.commands.admin.Greeting;
import de.eldoria.shepard.contexts.commands.admin.ShowKeyword;
import de.eldoria.shepard.contexts.commands.botconfig.ContextInfo;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContext;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContextGuild;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContextUsers;
import de.eldoria.shepard.contexts.commands.exklusive.IsHaddeWorking;
import de.eldoria.shepard.contexts.commands.exklusive.Meetings;
import de.eldoria.shepard.contexts.commands.fun.MagicConch;
import de.eldoria.shepard.contexts.commands.fun.Oha;
import de.eldoria.shepard.contexts.commands.fun.Owo;
import de.eldoria.shepard.contexts.commands.fun.Owod;
import de.eldoria.shepard.contexts.commands.fun.RandomJoke;
import de.eldoria.shepard.contexts.commands.fun.Sayd;
import de.eldoria.shepard.contexts.commands.fun.Uwu;
import de.eldoria.shepard.contexts.commands.fun.Uwud;
import de.eldoria.shepard.contexts.commands.util.GetRaw;
import de.eldoria.shepard.contexts.commands.util.Help;
import de.eldoria.shepard.contexts.commands.util.HireMe;
import de.eldoria.shepard.contexts.commands.util.ListServer;
import de.eldoria.shepard.contexts.commands.util.Test;
import de.eldoria.shepard.contexts.commands.util.UserInfo;
import de.eldoria.shepard.contexts.keywords.keyword.AmIRight;
import de.eldoria.shepard.contexts.keywords.keyword.CommanderQuestion;
import de.eldoria.shepard.contexts.keywords.keyword.Communism;
import de.eldoria.shepard.contexts.keywords.keyword.DariNope;
import de.eldoria.shepard.contexts.keywords.keyword.DariYes;
import de.eldoria.shepard.contexts.keywords.keyword.Mlp;
import de.eldoria.shepard.contexts.keywords.keyword.Normandy;
import de.eldoria.shepard.contexts.keywords.keyword.Nudes;
import de.eldoria.shepard.contexts.keywords.keyword.Thing;
import de.eldoria.shepard.listener.CommandListener;
import de.eldoria.shepard.listener.JoinListener;
import de.eldoria.shepard.listener.KeyWordListener;
import de.eldoria.shepard.listener.LogListener;
import de.eldoria.shepard.listener.MessageSniffer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class ShepardBot {
    private static JDA jda;

    private static Config config;

    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        // Note: It is important to register your ReadyListener before building

        config = Loader.getConfigLoader().getConfig();

        try {
            initiateJda();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void initiateJda() throws LoginException, InterruptedException {
        jda = new JDABuilder(config.getToken())
                //JoinListener
                .addEventListeners(new CommandListener())
                .addEventListeners(new KeyWordListener())
                .addEventListeners(new MessageSniffer())
                .addEventListeners(new LogListener())
                .addEventListeners(new JoinListener())
                //Commands
                //botSettings
                .addEventListeners(new ContextInfo())
                .addEventListeners(new ManageContext())
                .addEventListeners(new ManageContextUsers())
                .addEventListeners(new ManageContextGuild())
                //admin
                .addEventListeners(new Invite())
                .addEventListeners(new Greeting())
                .addEventListeners(new Prefix())
                .addEventListeners(new ShowKeyword())
                //exklusive
                .addEventListeners(new IsHaddeWorking())
                .addEventListeners(new Meetings())
                //Fun
                .addEventListeners(new MagicConch())
                .addEventListeners(new Oha())
                .addEventListeners(new Owo())
                .addEventListeners(new Owod())
                .addEventListeners(new RandomJoke())
                .addEventListeners(new Sayd())
                .addEventListeners(new Uwu())
                .addEventListeners(new Uwud())
                //util
                .addEventListeners(new Help())
                .addEventListeners(new HireMe())
                .addEventListeners(new Test())
                .addEventListeners(new ListServer())
                .addEventListeners(new UserInfo())
                .addEventListeners(new GetRaw())
                //Keywords
                .addEventListeners(new Nudes())
                .addEventListeners(new Communism())
                .addEventListeners(new Normandy())
                .addEventListeners(new AmIRight())
                .addEventListeners(new Mlp())
                .addEventListeners(new CommanderQuestion())
                .addEventListeners(new Thing())
                .addEventListeners(new DariNope())
                .addEventListeners(new DariYes())
                //ReactionMessages
                .addEventListeners(new Test())
                .build();

        // optionally block until JDA is ready
        jda.awaitReady();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        CommandCollection.getInstance().debug();
        KeyWordCollection.getInstance().debug();
    }

    /**
     * Gets the jda.
     * @return JDA object
     */
    public static JDA getJDA() {
        return jda;
    }

    /**
     * Get the config.
     * @return Config object
     */
    public static Config getConfig() {
        return config;
    }
}
