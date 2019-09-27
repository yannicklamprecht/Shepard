package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MockingSpongebob extends Command {
    private static final String BASE_URL = "https://mockingspongebob.org/";

    public MockingSpongebob() {
        this.commandName = "mockingSpongebob";
        this.commandDesc = "MoCkInG SpOnGeBoB";
        this.commandAliases = new String[] {"msb", "mock"};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }
        try {
            sendImage(String.join("%20", args), messageContext.getChannel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendImage(String text, MessageChannel channel) throws IOException {
        String urlString = BASE_URL + text;
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        InputStream inputStream = connection.getInputStream();
        channel.sendFile(inputStream, "mock" + UUID.randomUUID() + ".jpg").queue();
    }
}
