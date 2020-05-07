package de.eldoria.shepard.database;

import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;

@Slf4j
public class ConnectionPool implements ReqConfig, ReqInit {
    private final PGPoolingDataSource source = new PGPoolingDataSource();
    private Config config;

    /**
     * Create a new connection pool.
     */
    public ConnectionPool() {
    }

    /**
     * Get the data source of the pool.
     *
     * @return data source obbject
     */
    public DataSource getSource() {
        return source;
    }

    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void init() {
        source.setDataSourceName("PG Source");
        source.setServerName(config.getDatabase().getAddress());
        source.setDatabaseName(config.getDatabase().getDb());
        source.setUser(config.getDatabase().getUsername());
        source.setPassword(config.getDatabase().getPassword());
        source.setPortNumber(config.getDatabase().getPort());
        source.setMaxConnections(20);
        source.setInitialConnections(2);
        log.info("Created new connectionpool for {}@{}:{}/{}",
                config.getDatabase().getUsername(), config.getDatabase().getAddress(),
                config.getDatabase().getPort(), config.getDatabase().getDb());
    }
}
