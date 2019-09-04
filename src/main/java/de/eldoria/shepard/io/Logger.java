package de.eldoria.shepard.io;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.lang.System.lineSeparator;

public class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    private Path logFile;


    /**
     * Creates a new Logger object to log messages async to a file.
     */
    public Logger() {
        File shepardJar = new File(ClassLoader.getSystemClassLoader()
                .getResource(".").getPath());
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();

        Path logs = Paths.get(home + "/logs");

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
     *
     * @param message Message to write
     */
    public void error(String message) {
        Arrays.stream(message.split(lineSeparator())).forEach(s -> log(s, LogType.ERROR));
    }

    /**
     * Writes a info to log.
     *
     * @param message Message to write
     */
    public void info(String message) {
        Arrays.stream(message.split(lineSeparator())).forEach(s -> log(s, LogType.INFO));
    }

    /**
     * Writes a command to log.
     *
     * @param message Message to write
     */
    public void command(String message) {
        Arrays.stream(message.split(lineSeparator())).forEach(s -> log(s, LogType.COMMAND));
    }

    private void log(String message, LogType type) {
        String header = "[" + LocalDateTime.now().format(FORMATTER) + " " + type.toString() + "]: ";
        System.out.println(header + message);

        AsynchronousFileChannel fileChannel;
        try {
            fileChannel = AsynchronousFileChannel.open(
                    logFile, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println("Could open log file." + lineSeparator());
            e.printStackTrace();
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put((header + message + lineSeparator()).getBytes());
        buffer.flip();

        try {
            fileChannel.write(buffer, fileChannel.size());
        } catch (IOException e) {
            System.out.println("Failed writing to log." + lineSeparator());
            e.printStackTrace();
            buffer.clear();
            return;
        }
        buffer.clear();
    }
}