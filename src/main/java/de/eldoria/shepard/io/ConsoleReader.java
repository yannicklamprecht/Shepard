package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;

import java.util.Scanner;

public class ConsoleReader implements Runnable {

    private static Thread thread;

    private Scanner inputReader = new Scanner(System.in);

    /**
     * Creates a new console reader object.
     */
    public ConsoleReader() {
        System.out.println("Console reader started!");
        start();
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
