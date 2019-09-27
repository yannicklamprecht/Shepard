package de.eldoria.shepard.register;

import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.commands.admin.Changelog;
import de.eldoria.shepard.contexts.commands.admin.Greeting;
import de.eldoria.shepard.contexts.commands.admin.Invite;
import de.eldoria.shepard.contexts.commands.admin.ManageQuote;
import de.eldoria.shepard.contexts.commands.admin.Permission;
import de.eldoria.shepard.contexts.commands.admin.Prefix;
import de.eldoria.shepard.contexts.commands.admin.ShowKeyword;
import de.eldoria.shepard.contexts.commands.admin.Ticket;
import de.eldoria.shepard.contexts.commands.admin.TicketSettings;
import de.eldoria.shepard.contexts.commands.botconfig.ContextInfo;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContext;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContextGuild;
import de.eldoria.shepard.contexts.commands.botconfig.ManageContextUsers;
import de.eldoria.shepard.contexts.commands.exklusive.IsHaddeWorking;
import de.eldoria.shepard.contexts.commands.exklusive.Meetings;
import de.eldoria.shepard.contexts.commands.fun.GuessGame;
import de.eldoria.shepard.contexts.commands.admin.GuessGameConfig;
import de.eldoria.shepard.contexts.commands.fun.LargeEmote;
import de.eldoria.shepard.contexts.commands.fun.MagicConch;
import de.eldoria.shepard.contexts.commands.fun.MockingSpongebob;
import de.eldoria.shepard.contexts.commands.fun.Oha;
import de.eldoria.shepard.contexts.commands.fun.Owo;
import de.eldoria.shepard.contexts.commands.fun.Owod;
import de.eldoria.shepard.contexts.commands.fun.Quote;
import de.eldoria.shepard.contexts.commands.fun.RandomJoke;
import de.eldoria.shepard.contexts.commands.fun.Sayd;
import de.eldoria.shepard.contexts.commands.fun.Someone;
import de.eldoria.shepard.contexts.commands.fun.Uwu;
import de.eldoria.shepard.contexts.commands.fun.Uwud;
import de.eldoria.shepard.contexts.commands.util.GetRaw;
import de.eldoria.shepard.contexts.commands.util.Help;
import de.eldoria.shepard.contexts.commands.util.HireMe;
import de.eldoria.shepard.contexts.commands.util.Home;
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
import de.eldoria.shepard.contexts.keywords.keyword.SomeoneKeyword;
import de.eldoria.shepard.contexts.keywords.keyword.Thing;

import java.util.ArrayList;
import java.util.List;

public final class ContextRegister {
    private static ContextRegister instance;

    private final List<ContextSensitive> contextSensitives = new ArrayList<>();

    private ContextRegister() {
        registerBotConfigCommands();
        registerAdminCommands();
        registerExclusiveCommands();
        registerUtilCommands();
        registerFunCommands();

        registerKeywords();
    }

    private void registerKeywords() {
        contextSensitives.add(new AmIRight());
        contextSensitives.add(new CommanderQuestion());
        contextSensitives.add(new Communism());
        contextSensitives.add(new DariNope());
        contextSensitives.add(new DariYes());
        contextSensitives.add(new Mlp());
        contextSensitives.add(new Normandy());
        contextSensitives.add(new Nudes());
        contextSensitives.add(new Thing());
        contextSensitives.add(new SomeoneKeyword());
    }

    private void registerUtilCommands() {
        contextSensitives.add(new GetRaw());
        contextSensitives.add(new Help());
        contextSensitives.add(new HireMe());
        contextSensitives.add(new ListServer());
        contextSensitives.add(new Test());
        contextSensitives.add(new UserInfo());
        contextSensitives.add(new Home());
    }

    private void registerFunCommands() {
        contextSensitives.add(new MagicConch());
        contextSensitives.add(new MockingSpongebob());
        contextSensitives.add(new Oha());
        contextSensitives.add(new Owo());
        contextSensitives.add(new Owod());
        contextSensitives.add(new RandomJoke());
        contextSensitives.add(new Sayd());
        contextSensitives.add(new Uwu());
        contextSensitives.add(new Uwud());
        contextSensitives.add(new Quote());
        contextSensitives.add(new Someone());
        contextSensitives.add(new LargeEmote());
        contextSensitives.add(new GuessGame());
    }

    private void registerExclusiveCommands() {
        contextSensitives.add(new IsHaddeWorking());
        contextSensitives.add(new Meetings());
    }

    private void registerBotConfigCommands() {
        contextSensitives.add(new ContextInfo());
        contextSensitives.add(new ManageContext());
        contextSensitives.add(new ManageContextGuild());
        contextSensitives.add(new ManageContextUsers());
    }

    private void registerAdminCommands() {
        contextSensitives.add(new Greeting());
        contextSensitives.add(new Invite());
        contextSensitives.add(new Prefix());
        contextSensitives.add(new ShowKeyword());
        contextSensitives.add(new TicketSettings());
        contextSensitives.add(new Ticket());
        contextSensitives.add(new Changelog());
        contextSensitives.add(new ManageQuote());
        contextSensitives.add(new Permission());
        contextSensitives.add(new GuessGameConfig());

    }

    private static void getInstance() {
        if (instance == null) {
            instance = new ContextRegister();
        }
    }

    /**
     * Registers all contexts in command or keyword collection.
     */
    public static void registerContexts() {
        getInstance();
    }
}
