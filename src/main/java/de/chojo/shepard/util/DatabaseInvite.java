package de.chojo.shepard.util;

public class DatabaseInvite {

    private String code;
    private String name;
    private int count;

    public DatabaseInvite (String code, String name, int count) {
        this.code = code;
        this.name = name;
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
