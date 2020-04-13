package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
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
public class RandomJoke extends Command {

    /**
     * Creates a new random joke command object.
     */
    public RandomJoke() {
        super("joke",
                null,
                DESCRIPTION.tag,
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        try {
            MessageSender.sendSimpleTextBox(M_JOKE.tag, getRandomJoke(), messageContext.getTextChannel());
        } catch (IOException e) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getTextChannel());
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