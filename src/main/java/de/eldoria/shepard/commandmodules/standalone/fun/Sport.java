package de.eldoria.shepard.commandmodules.standalone.fun;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.fun.SportLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Sport extends Command implements Executable {
    private Activities activities;

    public Sport() {
        super("sport",
                null,
                SportLocale.DESCRIPTION.tag,
                CommandCategory.FUN);

        try (var in = ClassLoader.getSystemClassLoader().getResourceAsStream("sport/sport.json")) {
            try (JsonReader reader = new JsonReader(new InputStreamReader(in))) {
                activities = GSON.fromJson(reader, Activities.class);
            }
        } catch (IOException e) {
            log.error("Could not load sport data.", e);
        }
    }

    private static final Gson GSON = new GsonBuilder().create();


    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Activities.Activity activity = activities.activities[ThreadLocalRandom.current().nextInt(activities.activities.length)];
        int reps = ThreadLocalRandom.current().nextInt(activity.min, activity.max + 1);
        MessageEmbed build = new LocalizedEmbedBuilder(wrapper)
                .setTitle(activity.name + " | " + reps + " " + activity.type.localeCode)
                .setImage(activity.image)
                .setColor(Colors.Pastel.ORANGE)
                .setFooter("https://darebee.com/")
                .build();

        wrapper.getMessageChannel().sendMessage(build).queue();
    }

    private static class Activities {
        private Activity[] activities;

        private static class Activity {
            private String name;
            private ExceciseType type;
            private String image;
            private int min;
            private int max;
        }

        private enum ExceciseType {
            TIME(GeneralLocale.AD_SECONDS.tag), REPEAT(SportLocale.REPEAT.tag);
            private final String localeCode;

            ExceciseType(String localeCode) {
                this.localeCode = localeCode;
            }
        }
    }
}
