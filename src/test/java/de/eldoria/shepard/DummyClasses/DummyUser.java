package de.eldoria.shepard.DummyClasses;

import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.UserImpl;

import java.util.Random;

public class DummyUser extends UserImpl {

    public DummyUser(JDAImpl jda){
        super(new Random().nextLong() * -1, jda);
    }
}
