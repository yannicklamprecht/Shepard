package de.eldoria.shepard.io;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.Future;

public class Logger {

    private final String HOME;
    private Path logFile;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");


    /**
     * Creates a new Logger object to log messages async to a file.
     */
    public Logger() {
        File shepardJar = new File(ClassLoader.getSystemClassLoader()
                .getResource(".").getPath());
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        HOME = shepardFolder.toString();

        Path logs = Paths.get(HOME + "/logs");

        if (!Files.exists(logs)) {
            try {
                Files.createDirectory(logs);
            } catch (IOException e) {
                System.out.println("Directory for logs could not be created!");
                MessageSender.sendSimpleError("Directory for logs could not be created!",
                        Normandy.getErrorChannel());
            }
        }

        Path logFilePath = Paths.get(logs + "\\" + LocalDateTime.now().format(FORMATTER) + ".log");
        try {
            logFile = Files.createFile(logFilePath);
        } catch (IOException e) {
            MessageSender.sendSimpleError("Couldn't create log file!", Normandy.getErrorChannel());
            System.out.println("Couldn't create log file!");
        }
    }

    /**
     * Writes a error to log.
     * @param message Message to write
     */
    public void error(String message) {
        Arrays.stream(message.split(System.lineSeparator())).forEach(s -> log(s, LogType.ERROR));
    }

    /**
     * Writes a info to log.
     * @param message Message to write
     */
    public void info(String message) {
        Arrays.stream(message.split(System.lineSeparator())).forEach(s -> log(s, LogType.INFO));
    }

    private void log(String message, LogType type) {
        String header = "[" + LocalDateTime.now().format(FORMATTER) + " " + type.toString() + "]: ";
        AsynchronousFileChannel fileChannel;
        try {
            fileChannel = AsynchronousFileChannel.open(
                    logFile, StandardOpenOption.WRITE);
        } catch (IOException e) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put((header + message + System.lineSeparator()).getBytes());
        buffer.flip();

        Future<Integer> operation;
        try {
            operation = fileChannel.write(buffer, fileChannel.size());
        } catch (IOException e) {
            buffer.clear();
            return;
        }
        buffer.clear();
    }
}