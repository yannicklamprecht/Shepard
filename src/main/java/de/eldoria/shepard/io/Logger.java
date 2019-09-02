package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;
import org.mortbay.log.Log;

import java.io.File;

public class Logger {
    public Logger() {
        String path = new File(ShepardBot.class.getProtectionDomain().getCodeSource().getLocation().getPath().toURI());
    }
}
