package de.chojo.shepard.Collections;


import de.chojo.shepard.DatabaseConnector;

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

    private DatabaseCache(){
        databaseServers = DatabaseConnector.getInternalServers();
    }

    public int getServerId(String discordId){
        return databaseServers.get(discordId);
    }

    public HashMap<String, Integer> getDatabaseServers() {
        return databaseServers;
    }

    public void setDatabaseServers(HashMap<String, Integer> databaseServers) {
        this.databaseServers = databaseServers;
    }
}
