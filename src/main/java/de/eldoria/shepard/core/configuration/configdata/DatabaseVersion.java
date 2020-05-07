package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

@Data
public class DatabaseVersion {
    /**
     * List of required schemas
     */
    String[] requiredSchemas;
    /**
     * version of database.
     */
    String version;
    /**
     * Patch version of database.
     */
    int patch;
}
