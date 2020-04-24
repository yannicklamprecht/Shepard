package de.eldoria.shepard.core;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.core.configuration.configdata.DatabaseVersion;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.stream.Stream;

@Slf4j
public class SQLUpdater implements ReqConfig, ReqDataSource, ReqInit {

    private DataSource source;
    private Config config;

    @Override
    public void init() {
        checkDatabasePersistence();

        VersionInfo versionInfo = getVersionInfo();

        // Only update if the major version matches.
        if (versionInfo.getVersion().equals(config.getDatabase().getDatabaseVersion().getVersion())) {
            log.info("database version is equal. Searching for patch.");
        } else {
            throw new Error("Database and config Version does not match. Database: "
                    + versionInfo.getVersion() + " | "
                    + " Config: " + config.getDatabase().getDatabaseVersion().getVersion());
        }

        OptionalInt optionalPatchNumber = ArgumentParser.parseInt(versionInfo.getPatch());

        if (optionalPatchNumber.isEmpty()) {
            throw new Error("patch version is not an integer!");
        }

        int databasePatch = optionalPatchNumber.getAsInt();

        // Check for new patches.
        int requiredPatch = config.getDatabase().getDatabaseVersion().getPatch();
        if (databasePatch < requiredPatch) {
            log.info("Found different patch versions. Will perform update on database.");
            try {
                performUpdate(versionInfo.getVersion(), databasePatch, requiredPatch);
            } catch (SQLException e) {
                throw new Error("Database update failed!");
            }
            log.info("Database update was successful!");
        } else {
            log.info("Database is up to date. No update is required!");
        }
    }

    private void performUpdate(String version, int databasePatch, int requiredPatch) throws SQLException {
        for (int currentPatch = databasePatch; currentPatch < requiredPatch; currentPatch++) {
            String patchQuery = getPatchQuery(version, currentPatch + 1);
            try (var conn = source.getConnection(); var statement = conn.prepareStatement(patchQuery)) {
                statement.execute();
            } catch (SQLException e) {
                log.error("Database update failed", e);
                throw e;
            }
            log.info("Deployed patch number {} to database", currentPatch + 1);
            updateVersion(version, currentPatch + 1);
        }

    }

    private void checkDatabasePersistence() {
        for (var name : config.getDatabase().getDatabaseVersion().getRequiredSchemas()) {
            boolean schemaExists = schemaExists(name);
            if (!schemaExists) {
                log.info("Database is not persistent. Do you want to perform a automatic creation of the required schemas?\n" +
                        "WARNING: This can result in data loss if a database is already setup.\n" +
                        "[y/n]");
                String line = new Scanner(System.in).nextLine();
                if (line.equalsIgnoreCase("y")) {
                    createDatabase();
                } else {
                    throw new Error("Database is not persistent. Please perform a manual update.\n"
                            + "The database schema " + name + " is missing.");
                }
            }
        }
    }

    /**
     * Drops all schemas listed in {@link DatabaseVersion#getRequiredSchemas()}.
     * Loads the base database file inside the folder of the current version in {@link DatabaseVersion#getVersion()}
     * Initializes the version with the current version and patch number 0.
     */
    private void createDatabase() {
        // Drop old schemas to avoid conflics
        for (var name : config.getDatabase().getDatabaseVersion().getRequiredSchemas()) {
            String query = "DROP SCHEMA IF EXISTS " + name + " CASCADE";
            try (var conn = source.getConnection(); var statement = conn
                    .prepareStatement(query)) {
                statement.execute();
            } catch (SQLException e) {
                log.error("Failed to drop old schema " + name, e);
                throw new Error("Failed to drop old schema " + name);
            }
            log.info("Dropped schema {}", name);
        }

        // Drop version table.
        String query = "DROP TABLE IF EXISTS public.version CASCADE";
        try (var conn = source.getConnection(); var statement = conn
                .prepareStatement(query)) {
            statement.execute();
        } catch (SQLException e) {
            log.error("Failed to drop version table");
            throw new Error("Failed to drop version table");
        }
        log.info("Dropped version table!");


        String version = config.getDatabase().getDatabaseVersion().getVersion();
        // Load base file
        String baseQuery = getBaseQuery(version);

        try (var conn = source.getConnection(); var statement = conn.prepareStatement(baseQuery)) {
            statement.execute();
        } catch (SQLException e) {
            log.error("Failed to create base database", e);
            throw new Error("Failed to create base database.");
        }

        log.info("Setup of new base database is done.");

        // initialize database with patch version 0
        updateVersion(version, 0);
    }

    /**
     * Checks if a schema exists in the database
     *
     * @param name name of schema
     * @return true if the schema exists
     */
    private boolean schemaExists(String name) {
        try (var conn = source.getConnection(); var statement = conn
                .prepareStatement("SELECT EXISTS(SELECT schema_name FROM information_schema.schemata WHERE schema_name = ?)")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean("exists");
        } catch (SQLException e) {
            log.error("Could not check if schema exists in database!", e);
        }
        throw new Error("Failed to check database!");
    }

    /**
     * Update the current database Version.
     *
     * @param version new version of database
     * @param patch   new patch of database
     */
    private void updateVersion(String version, int patch) {
        int versionsCount;
        try (var conn = source.getConnection(); var statement = conn
                .prepareStatement("SELECT count(*) FROM public.version")) {
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            versionsCount = resultSet.getInt(1);
        } catch (SQLException e) {
            log.error("Failed change database version!", e);
            throw new Error("Failed change database version");
        }
        if (versionsCount == 0) {
            try (var conn = source.getConnection(); var statement = conn
                    .prepareStatement("INSERT INTO public.version (version, patch) VALUES (?, ?)")) {
                statement.setString(1, version);
                statement.setString(2, patch + "");
                statement.execute();
            } catch (SQLException e) {
                log.error("Failed change database version!", e);
                throw new Error("Failed change database version");
            }
        } else if (versionsCount > 1) {
            throw new Error("More than one version is defined in database!");
        } else {
            try (var conn = source.getConnection(); var statement = conn
                    .prepareStatement("UPDATE public.version SET version = ?, patch = ?")) {
                statement.setString(1, version);
                statement.setString(2, patch + "");
                statement.execute();
            } catch (SQLException e) {
                log.error("Failed change database version!", e);
                throw new Error("Failed change database version");
            }
        }


        log.info("Set database to version {} patch {}!", version, patch);
    }

    private VersionInfo getVersionInfo() {
        try (var conn = source.getConnection(); var statement = conn
                .prepareStatement("select version, patch from public.version limit 1")) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new VersionInfo(resultSet.getString("version"), resultSet.getString("patch"));
            } else {
                throw new Error("Could not retrieve database version!");
            }
        } catch (SQLException e) {
            log.error("Could not check if schema exists in database!", e);
        }
        throw new Error("Could not retrieve database version!");
    }


    @Override
    public void addConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Data
    private static class VersionInfo {
        private String version;
        private String patch;

        public VersionInfo(String version, String patch) {
            this.version = version;
            this.patch = patch;
        }
    }


    private String getPatchQuery(String version, int patch) {
        StringBuilder contentBuilder = new StringBuilder();

        String home = new File(".").getAbsoluteFile().getParentFile().toString();

        Path patchfile = Paths.get(home, "database", version, "patch-" + patch + ".sql");

        try (Stream<String> stream = Files.lines(patchfile, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new Error("Can't load patch " + patch + " of version " + version + "! (" + patchfile.toString() + ")");
        }
        return contentBuilder.toString();
    }

    private String getBaseQuery(String version) {
        StringBuilder contentBuilder = new StringBuilder();

        String home = new File(".").getAbsoluteFile().getParentFile().toString();

        Path baseFile = Paths.get(home, "database", version, "base.sql");

        try (Stream<String> stream = Files.lines(baseFile, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new Error("Can't load base of version " + version + "! (" + baseFile.toString() + ")");
        }
        return contentBuilder.toString();
    }
}
