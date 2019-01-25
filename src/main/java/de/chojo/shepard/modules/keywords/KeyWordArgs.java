package de.chojo.shepard.modules.keywords;

public class KeyWordArgs {
    private final String key;
    private final Keyword keyword;

    public KeyWordArgs(String key, Keyword keyword) {
        this.key = key;
        this.keyword = keyword;
    }

    public String getKey() {
        return key;
    }

    public Keyword getKeyword() {
        return keyword;
    }
}
