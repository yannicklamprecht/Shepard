package de.chojo.shepard;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.configuration.Config;
import de.chojo.shepard.configuration.Loader;
import de.chojo.shepard.contexts.commands.admin.ShowKeyword;
import de.chojo.shepard.contexts.commands.botconfig.ManageContext;
import de.chojo.shepard.contexts.commands.botconfig.ManageContextUsers;
import de.chojo.shepard.contexts.commands.admin.RegisterInviteLink;
import de.chojo.shepard.contexts.commands.admin.SetGreetingChannel;
import de.chojo.shepard.contexts.commands.admin.SetPrefix;
import de.chojo.shepard.contexts.commands.exklusive.*;
import de.chojo.shepard.contexts.commands.fun.MagicConch;
import de.chojo.shepard.contexts.commands.fun.Oha;
import de.chojo.shepard.contexts.commands.fun.Owo;
import de.chojo.shepard.contexts.commands.fun.Owod;
import de.chojo.shepard.contexts.commands.fun.RandomJoke;
import de.chojo.shepard.contexts.commands.fun.Sayd;
import de.chojo.shepard.contexts.commands.fun.Uwu;
import de.chojo.shepard.contexts.commands.fun.Uwud;
import de.chojo.shepard.contexts.commands.util.GetRaw;
import de.chojo.shepard.contexts.commands.util.Help;
import de.chojo.shepard.contexts.commands.util.HireMe;
import de.chojo.shepard.contexts.commands.util.ListServer;
import de.chojo.shepard.contexts.commands.util.Test;
import de.chojo.shepard.contexts.commands.util.UserInfo;
import de.chojo.shepard.contexts.keywords.keyword.AmIRight;
import de.chojo.shepard.contexts.keywords.keyword.CommanderQuestion;
import de.chojo.shepard.contexts.keywords.keyword.Communism;
import de.chojo.shepard.contexts.keywords.keyword.DariNope;
import de.chojo.shepard.contexts.keywords.keyword.DariYes;
import de.chojo.shepard.contexts.keywords.keyword.Mlp;
import de.chojo.shepard.contexts.keywords.keyword.Normandy;
import de.chojo.shepard.contexts.keywords.keyword.Nudes;
import de.chojo.shepard.contexts.keywords.keyword.Thing;
import de.chojo.shepard.listener.CommandListener;
import de.chojo.shepard.listener.JoinListener;
import de.chojo.shepard.listener.KeyWordListener;
import de.chojo.shepard.listener.LogListener;
import de.chojo.shepard.listener.MessageSniffer;
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
                .addEventListeners(new ManageContext())
                .addEventListeners(new ManageContextUsers())
                //admin
                .addEventListeners(new SetGreetingChannel())
                .addEventListeners(new RegisterInviteLink())
                .addEventListeners(new SetPrefix())
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
