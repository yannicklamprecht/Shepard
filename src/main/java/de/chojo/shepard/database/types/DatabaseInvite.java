package de.chojo.shepard.database.types;

public class DatabaseInvite {
    String code;
    int usedCount;
    String source;

    public DatabaseInvite(String code, int usedCount, String source) {
        this.code = code;
        this.usedCount = usedCount;
        this.source = source;
    }

    public String getCode() {
        return code;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public String getSource() {
        return source;
    }
}
