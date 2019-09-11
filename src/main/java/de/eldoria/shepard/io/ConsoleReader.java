package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;

import java.util.Scanner;

public final class ConsoleReader implements Runnable {

    private static Thread thread;

    private static ConsoleReader instance;

    private Scanner inputReader = new Scanner(System.in);

    private ConsoleReader() {
        ShepardBot.getLogger().info("Console reader started!");
        waitForInput();
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

    private void waitForInput() {
        thread = new Thread(this);
        thread.start();
    }

    private void readText() {
        String input = inputReader.nextLine();
        if (input != null && !input.isEmpty()) {
            if (input.equalsIgnoreCase("shutdown")) {
                ShepardBot.getInstance().shutdown();
                ShepardBot.getLogger().info(input);
            }
            if (input.equals("ping")) {
                ShepardBot.getLogger().info("pong");
            }
            waitForInput();
        }
    }
}
