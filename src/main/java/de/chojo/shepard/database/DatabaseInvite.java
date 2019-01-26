package de.chojo.shepard.database;

public class DatabaseInvite {
    private String code;
    private String name;
    private int uses;

    public DatabaseInvite (String code, String name, int uses) {
        this.code = code;
        this.name = name;
        this.uses = uses;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getUses() {
        return uses;
    }
}
