package de.eldoria.shepard.DummyClasses;

import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

import java.util.Random;

public class DummyGuild extends GuildImpl {

    public DummyGuild(JDAImpl jda) {
        super(jda, new Random().nextLong() * -1);
    }
}
