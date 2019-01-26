package de.chojo.shepard.modules.keywords;

/**
 * A mapping of a key to a keyword.
 */
public class KeyWordArgs {
    private final String key;
    private final Keyword keyword;

    /**
     * Create a new instance with a given key and a given keyword.
     *
     * @param key the key of the KeyWordArgs.
     * @param keyword the keyword of the KeyWordArgs.
     */
    public KeyWordArgs(String key, Keyword keyword) {
        this.key = key;
        this.keyword = keyword;
    }

    /**
     * Get the key of the KeyWordArgs.
     *
     * @return the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the keyword of the KeyWordArgs.
     *
     * @return the keyword.
     */
    public Keyword getKeyword() {
        return keyword;
    }
}
