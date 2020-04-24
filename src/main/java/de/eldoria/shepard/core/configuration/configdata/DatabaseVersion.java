package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

@Data
public class DatabaseVersion {
    String[] requiredSchemas;
    String version;
    int patch;
}
