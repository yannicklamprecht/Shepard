package de.chojo.shepard.database.types;

import de.chojo.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Greeting {
    String text;
    TextChannel channel = null;

    public Greeting(String guildId, String channelId, String text){
        this.text = text;

        Guild guild = ShepardBot.getJDA().getGuildById(guildId);
        if(guild != null){
            this.channel = guild.getTextChannelById(channelId);
        }
    }

    public String getText() {
        return text;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
