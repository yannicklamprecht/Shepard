package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.Messages;
import de.chojo.shepard.modules.commands.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomJoke extends Command {
    public RandomJoke() {
        commandName = "joke";
        commandAliases = null;
        commandDesc = "Hehe";
        args = null;
    }

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        try {
            Messages.sendSimpleTextBox("Random Joke", getRandomJoke(), receivedEvent.getChannel());
        } catch (IOException e) {
            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Error", e.getMessage(), false)}, receivedEvent.getChannel());
        }
        return true;
    }

    private String getRandomJoke() throws IOException {
        URL url = new URL("https://icanhazdadjoke.com/");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        con.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());

        return jsonResponse.getString("joke");
    }
}