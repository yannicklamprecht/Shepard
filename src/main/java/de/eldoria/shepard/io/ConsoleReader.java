package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

public class ConsoleReader implements Runnable {

    private static Thread thread;

    private static ConsoleReader instance;

    private Scanner inputReader = new Scanner(System.in);

    private ConsoleReader() {
        ShepardBot.getLogger().info("Console reader started!");
        start();
    }

    /**
     * Initializes the console reader.
     */
    public static void initialize() {
        if (instance == null) {
            instance = new ConsoleReader();
        }
    }

    @Override
    public void run() {
        readText();
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private void readText() {
        String input = inputReader.nextLine();
        if (input != null && !input.isEmpty()) {
            if (input.equalsIgnoreCase("shutdown")) {
                ShepardBot.getInstance().shutdown();
                ShepardBot.getLogger().info(input);
            }
            readText();
        }
    }
}
