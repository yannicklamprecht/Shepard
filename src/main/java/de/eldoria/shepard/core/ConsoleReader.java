package de.eldoria.shepard.core;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShepard;
import de.eldoria.shepard.util.ExitCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public final class  ConsoleReader implements Runnable, ReqInit, ReqShepard {
    private static Thread thread;

    private final Scanner inputReader = new Scanner(System.in);
    private ShepardBot bot;

    /**
     * Create a new console reader object.
     */
    public ConsoleReader() {
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
                bot.shutdown(ExitCode.SHUTDOWN);
                return;
            }
            if (input.equalsIgnoreCase("restart")) {
                bot.shutdown(ExitCode.RESTART);
                return;
            }
        }
    }

    @Override
    public void init() {
        log.info("Console reader started!");
        start();
    }

    @Override
    public void addShepard(ShepardBot bot) {
        this.bot = bot;
    }
}
