package de.eldoria.shepard.database;

import javax.sql.DataSource;

public class QueryObject {
    /**
     * Data source object.
     */
    protected final DataSource source;

    /**
     * Default constructor for data object.
     *
     * @param source data source for information retrieval
     */
    protected QueryObject(DataSource source) {
        this.source = source;
    }
}
