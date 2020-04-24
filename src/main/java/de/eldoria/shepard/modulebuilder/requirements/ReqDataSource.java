package de.eldoria.shepard.modulebuilder.requirements;

import javax.sql.DataSource;

public interface ReqDataSource {
    /**
     * Add a data source to an object.
     * @param source data source object
     */
    void addDataSource(DataSource source);
}
