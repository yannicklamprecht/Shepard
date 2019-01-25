package de.chojo.shepard.database;


import java.util.HashMap;

public class DatabaseCache {

    private static DatabaseCache instance;
    private HashMap<String, Integer> databaseServers;
    public static DatabaseCache getInstance() {
        if (instance == null) {
            synchronized (DatabaseCache.class) {
                if (instance == null) {
                    instance = new DatabaseCache();
                }
            }
        }
        return instance;
    }

    public HashMap<String, Integer> getDatabaseServers() {
        return databaseServers;
    }

    public void setDatabaseServers(HashMap<String, Integer> databaseServers) {
        this.databaseServers = databaseServers;
    }
}
