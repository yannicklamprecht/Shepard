package de.chojo.shepard.database;

public enum ListType {
    WHITELIST, BLACKLIST;

    public static ListType getType(String s) {
        for (ListType t : ListType.values()) {
            if(s.equalsIgnoreCase(t.toString())){
                return t;
            }
        }
        return null;
    }
}
