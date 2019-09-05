package de.eldoria.shepard.io;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.messagehandler.MessageSender;
import jdk.jfr.StackTrace;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.FileNotFoundException;
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

import static java.lang.System.err;
import static java.lang.System.lineSeparator;
import static java.lang.System.out;

public class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    private Path logFile;


    /**
     * Creates a new Logger object to log messages async to a file.
     */
    public Logger() {
        File shepardJar = new File(".");
        File shepardFolder = shepardJar.getAbsoluteFile().getParentFile();
        String home = shepardFolder.toString();

        Path logs = Paths.get(home + "/logs");

        if (!Files.exists(logs)) {
            out.println("No logs directory found!");

            try {
                out.println("Trying to create directory");
                Files.createDirectory(logs);
            } catch (IOException e) {
                out.println("Directory for logs could not be created!");
                return;
            }
            out.println("Directory created!");
        }


        Path logFilePath = Paths.get(logs + "/" + LocalDateTime.now().format(FORMATTER) + ".log");
        try {
            out.println("Trying to create log File");
            logFile = Files.createFile(logFilePath);
        } catch (IOException e) {
            MessageSender.sendSimpleError("Couldn't create log file!", Normandy.getErrorChannel());
            out.println("Couldn't create log file!");
            return;
        }

        info("Logger initialized");

    }

    /**
     * Writes a error to log.
     *
     * @param exception exception to log
     */
    public void error(Throwable exception) {
        error(ExceptionUtils.getStackTrace(exception));
    }

    /**
     * Writes a error to log.
     *
     * @param message   Message to write
     * @param exception exception to log
     */
    public void error(String message, Throwable exception) {
        error(message + lineSeparator() + ExceptionUtils.getStackTrace(exception));
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
        if (logFile == null) {
            out.println("No log directory found. Logging is disabled.");
        }

        String header = "[" + LocalDateTime.now().format(FORMATTER) + " " + type.toString() + "]: ";
        out.println(header + message);

        AsynchronousFileChannel fileChannel;
        try {
            fileChannel = AsynchronousFileChannel.open(
                    logFile, StandardOpenOption.WRITE);
        } catch (IOException e) {
            out.println("Could open log file." + lineSeparator());
            e.printStackTrace();
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate((header + message + lineSeparator()).getBytes().length);
        buffer.put((header + message + lineSeparator()).getBytes());
        buffer.flip();

        try {
            fileChannel.write(buffer, fileChannel.size());
        } catch (IOException e) {
            out.println("Failed writing to log." + lineSeparator());
            e.printStackTrace();
            buffer.clear();
            return;
        }
        buffer.clear();

    }
}