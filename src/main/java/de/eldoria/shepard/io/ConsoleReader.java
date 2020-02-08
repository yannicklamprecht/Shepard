package de.eldoria.shepard.io;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.util.ExitCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public final class ConsoleReader implements Runnable {

    private static Thread thread;
    private static ConsoleReader instance = new ConsoleReader();

    private final Scanner inputReader = new Scanner(System.in);

    private ConsoleReader() {
        log.info("Console reader started!");
        start();
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
                log.info(input);
                ShepardBot.getInstance().shutdown(ExitCode.SHUTDOWN);
                return;
            }
            if (input.equalsIgnoreCase("restart")) {
                log.info(input);
                ShepardBot.getInstance().shutdown(ExitCode.RESTART);
                return;
            }
        }
    }
}
