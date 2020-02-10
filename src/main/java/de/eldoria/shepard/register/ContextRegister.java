package de.eldoria.shepard.register;

import de.eldoria.shepard.contexts.commands.admin.*;
import de.eldoria.shepard.contexts.commands.botconfig.*;
import de.eldoria.shepard.contexts.commands.exclusive.PrivateAnswer;
import de.eldoria.shepard.contexts.commands.exclusive.SendPrivateMessage;
import de.eldoria.shepard.contexts.commands.fun.*;
import de.eldoria.shepard.contexts.commands.util.*;

public final class ContextRegister {

    private ContextRegister() {
    }

    /*private static void registerKeywords() {
        new AmIRight();
        new CommanderQuestion();
        new Communism();
        new DariNope();
        new DariYes();
        new Mlp();
        new Normandy();
        new Nudes();
        new Thing();
        new SomeoneKeyword();
    }*/

    private static void registerUtilCommands() {
        new Reminder();
        new UserInfo();
        new Help();
        new HireMe();
        new Vote();
        new Home();
        new SystemInfo();
        new GetRaw();
        new Test();
    }

    private static void registerFunCommands() {
        new MagicConch();
        new MockingSpongebob();
        new Oha();
        new Owo();
        new RandomJoke();
        new Say();
        new Uwu();
        new Quote();
        new Someone();
        new LargeEmote();
        new GuessGame();
        new Kudos();
        new KudoLottery();
        new KudoGamble();
    }

    private static void registerExclusiveCommands() {
        //new IsHaddeWorking();
        //new Meetings();
    }

    private static void registerBotConfigCommands() {
        new ContextInfo();
        new ManageContext();
        new ManageContextGuild();
        new ManageContextUsers();
        new Upgrade();
        new Restart();
    }

    private static void registerAdminCommands() {
        new Greeting();
        new Invite();
        new Prefix();
        new ShowKeyword();
        new TicketSettings();
        new Ticket();
        new Changelog();
        new ManageQuote();
        new Permission();
        new GuessGameConfig();
        new BotPresence();
        new PrivateAnswer();
        new SendPrivateMessage();
        new Monitoring();
        new Language();
        new RepeatCommand();
        new McPing();
    }

    /**
     * Registers all contexts in command or keyword collection.
     */
    public static void registerContexts() {
        registerBotConfigCommands();
        registerAdminCommands();
        registerExclusiveCommands();
        registerUtilCommands();
        registerFunCommands();

        //registerKeywords();
    }
}
