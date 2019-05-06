package de.chojo.shepard;

import de.chojo.shepard.listener.*;
import de.chojo.shepard.modules.commands.fun.*;
import de.chojo.shepard.modules.commands.admin.RegisterInviteLink;
import de.chojo.shepard.modules.commands.admin.SetGreetingChannel;
import de.chojo.shepard.modules.commands.admin.SetPrefix;
import de.chojo.shepard.modules.commands.exklusive.*;
import de.chojo.shepard.modules.commands.util.*;
import de.chojo.shepard.modules.keywords.keyword.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class ShepardBot {
    private static JDA jda;

    public static void main(String[] args)
            throws LoginException, InterruptedException {
        org.apache.log4j.BasicConfigurator.configure();
        // Note: It is important to register your ReadyListener before building
        jda = new JDABuilder("NTEyNDEzMDQ5ODk0NzMxNzgw.DxjtCg.6nF2czGITfrX-HHR4cN7eCfil7I")
                //JoinListener
                .addEventListeners(new CommandListener())
                .addEventListeners(new KeyWordListener())
                .addEventListeners(new MessageSniffer())
                .addEventListeners(new LogListener())
                .addEventListeners(new JoinListener())
                //Commands
                //admin
                .addEventListeners(new SetGreetingChannel())
                .addEventListeners(new RegisterInviteLink())
                .addEventListeners(new SetPrefix())
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
        } catch (Exception ignore) { }
    }

    public static JDA getJDA() {
        return jda;
    }
}
