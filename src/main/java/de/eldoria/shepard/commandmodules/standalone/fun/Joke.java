package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventWrapper;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static de.eldoria.shepard.localization.enums.commands.fun.RandomJokeLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.RandomJokeLocale.M_JOKE;


/**
 * Receives a rancom joke from a web api.
 */
public class Joke extends Command implements ExecutableAsync {

    /**
     * Creates a new random joke command object.
     */
    public Joke() {
        super("joke",
                null,
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        try {
            MessageSender.sendSimpleTextBox(M_JOKE.tag, getRandomJoke(), wrapper);
        } catch (IOException e) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, wrapper);
        }
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
        StringBuilder response = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        con.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());

        return jsonResponse.getString("joke");
    }
}