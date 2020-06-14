package de.eldoria.shepard.commandmodules.ban.types;

import lombok.Getter;

@Getter
public class BanDataType {
    private String guild_id;
    private String user_id;

    public BanDataType(String guild_id, String user_id) {
        this.guild_id = guild_id;
        this.user_id = user_id;
    }
}
