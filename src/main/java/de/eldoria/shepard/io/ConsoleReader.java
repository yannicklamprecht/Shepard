package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;

import java.util.Scanner;

public final class ConsoleReader implements Runnable {

    private static Thread thread;
    private static ConsoleReader instance;

    private final Scanner inputReader = new Scanner(System.in);

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
        } else {
            thread.interrupt();
        }
        thread.start();
    }

    private void readText() {
        while (true) {
            String input = inputReader.nextLine();
            if (input == null || input.isEmpty()) {
                continue;
            }
            if (input.equalsIgnoreCase("shutdown")) {
                ShepardBot.getInstance().shutdown();
                ShepardBot.getLogger().info(input);
                return;
            }
        }
    }
}
