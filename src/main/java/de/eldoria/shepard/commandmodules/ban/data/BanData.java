package de.eldoria.shepard.commandmodules.ban.data;

import de.eldoria.shepard.database.QueryObject;

import javax.sql.DataSource;

public class BanData extends QueryObject {
    /**
     * Default constructor for data object.
     *
     * @param source data source for information retrieval
     */
    public BanData(DataSource source) {
        super(source);
    }
}
