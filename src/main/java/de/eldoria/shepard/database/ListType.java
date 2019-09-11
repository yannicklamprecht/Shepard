package de.eldoria.shepard.database;

public enum ListType {
    /**
     * Indicated that list is used as whitelist.
     */
    WHITELIST,

    /**
     * Indicates that list is used as blacklist.
     */
    BLACKLIST;

    /**
     * Get list type from string.
     *
     * @param s string for lookup
     * @return list type or null if no match was found.
     */
    public static ListType getType(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
