package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MockingSpongebob extends Command {

    public MockingSpongebob() {
        this.commandName = "mockingSpongebob";
        this.commandDesc = "MoCkInG SpOnGeBoB";
        this.commandAliases = new String[] {"msb", "mock"};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            messageContext.getChannel().sendMessage("No text given. What's wrong with you?").queue();
            return;
        }
        String urlString = "https://mockingspongebob.org/" + String.join("%20", args);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            InputStream inputStream = connection.getInputStream();
            messageContext.getChannel().sendFile(inputStream, "mock" + UUID.randomUUID() + ".jpg").queue(message -> {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
