package de.eldoria.shepard.commandmodules;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.basemodules.MessageListener;
import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.basemodules.commanddispatching.CooldownManager;
import de.eldoria.shepard.basemodules.commanddispatching.dialogue.DialogHandler;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.core.ShepardCollections;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.database.ConnectionPool;
import de.eldoria.shepard.modulebuilder.ReqAssigner;
import lombok.Getter;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class SharedResources implements ReqAssigner {
    private final ShepardBot shepardBot;
    private final ShardManager shardManager;
    private final Config config;
    private DataSource dataSource;
    private ConnectionPool connectionPool;
    private ShepardCollections collections;
    private CooldownManager cooldownManager;
    private ArgumentParser parser;
    private ExecutionValidator validator;
    private Statistics statistics;
    private final List<ListenerAdapter> listeners = new ArrayList<>();
    private CommandHub commandHub;
    private MessageListener messageListener;
    private DialogHandler dialogHandler;

    private SharedResources(ShepardBot shepardBot, ShardManager shardManager, Config config) {
        this.shepardBot = shepardBot;
        this.shardManager = shardManager;
        this.config = config;
        this.dialogHandler = new DialogHandler();
    }

    /**
     * Build a new shared resources object.
     * All Objects which are not delivered will be created by this method.
     *
     * @param shepardBot bot instance
     * @param jda        jda instance
     * @param config     config.
     * @return a new initialized shared resources object.
     */
    public static SharedResources build(ShepardBot shepardBot, ShardManager jda, Config config) {
        SharedResources sharedResources = new SharedResources(shepardBot, jda, config);
        sharedResources.init();
        return sharedResources;
    }

    private void init() {

        // Req JDA
        collections = new ShepardCollections();
        addAndInit(collections, this);


        // Req Config
        connectionPool = new ConnectionPool();
        addAndInit(connectionPool, this);
        dataSource = connectionPool.getSource();

        // Req DataSource
        messageListener = new MessageListener();
        addAndInit(connectionPool, this);

        // Req DataSource
        validator = new ExecutionValidator();
        addAndInit(validator, this);

        // ReqDataSource
        cooldownManager = new CooldownManager();
        addAndInit(cooldownManager, this);

        // Req Config, Execution Valid, Cooldown Manager, DataSource
        commandHub = new CommandHub();
        addAndInit(commandHub, this);

        // Req JDA
        statistics = new Statistics();
        addAndInit(statistics, this);

        addAndInit(this, collections.getPrivateMessages(), collections.getNormandy());

        parser = new ArgumentParser();
        addAndInit(parser, this);

    }

    /**
     * Add a listener to the listener list.
     *
     * @param listener listener to add.
     */
    public void addListener(ListenerAdapter listener) {
        listeners.add(listener);
    }
}
