package de.chojo.shepard;

import de.chojo.shepard.Collections.ServerCollection;
import de.chojo.shepard.listener.LogListener;
import de.chojo.shepard.modules.commands.Fun.*;
import de.chojo.shepard.modules.commands.exklusive.*;
import de.chojo.shepard.modules.commands.util.*;
import de.chojo.shepard.listener.CommandListener;
import de.chojo.shepard.listener.KeyWordListener;
import de.chojo.shepard.listener.MessageSniffer;
import de.chojo.shepard.modules.keywords.keyword.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ShepardBot {

    private static JDA jda;

    public static void main(String[] args)
            throws LoginException, InterruptedException {
        org.apache.log4j.BasicConfigurator.configure();
        // Note: It is important to register your ReadyListener before building
        jda = new JDABuilder("NTEyNDEzMDQ5ODk0NzMxNzgw.DxjtCg.6nF2czGITfrX-HHR4cN7eCfil7I")
                //Listener
                .addEventListeners(new CommandListener())
                .addEventListeners(new KeyWordListener())
                .addEventListeners(new MessageSniffer())
                .addEventListeners(new LogListener())
                //Commands
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
                //Keywords
                .addEventListeners(new Nudes())
                .addEventListeners(new Communism())
                .addEventListeners(new Normandy())
                .addEventListeners(new AmIRight())
                .addEventListeners(new Mlp())
                .addEventListeners(new CommanderQuestion())
                .addEventListeners(new Thing())
                .build();


        // optionally block until JDA is ready
        jda.awaitReady();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {

        }


    }

    public static JDA getJDA() {
        return jda;
    }
}
