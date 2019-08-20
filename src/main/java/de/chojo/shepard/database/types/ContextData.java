package de.chojo.shepard.database.types;

import de.chojo.shepard.database.ListType;

public class ContextData {
    private boolean admin_only;
    private boolean nsfw;
    private boolean characterCheckActive;
    private ListType characterListType;
    private String[] characterList;
    private boolean guildCheckActive;
    private ListType guildListType;
    private String[] guildList;

    public boolean isAdmin_only() {
        return admin_only;
    }

    public void setAdmin_only(boolean admin_only) {
        this.admin_only = admin_only;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public boolean isCharacterCheckActive() {
        return characterCheckActive;
    }

    public void setCharacterCheckActive(boolean characterCheckActive) {
        this.characterCheckActive = characterCheckActive;
    }

    public ListType getCharacterListType() {
        return characterListType;
    }

    public void setCharacterListType(ListType characterListType) {
        this.characterListType = characterListType;
    }

    public String[] getCharacterList() {
        return characterList;
    }

    public void setCharacterList(String[] characterList) {
        this.characterList = characterList;
    }

    public boolean isGuildCheckActive() {
        return guildCheckActive;
    }

    public void setGuildCheckActive(boolean guildCheckActive) {
        this.guildCheckActive = guildCheckActive;
    }

    public ListType getGuildListType() {
        return guildListType;
    }

    public void setGuildListType(ListType guildListType) {
        this.guildListType = guildListType;
    }

    public String[] getGuildList() {
        return guildList;
    }

    public void setGuildList(String[] guildList) {
        this.guildList = guildList;
    }
}
