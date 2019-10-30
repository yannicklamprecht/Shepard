package de.eldoria.shepard.collections;

import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public final class LatestCommandsCollection {
    private static LatestCommandsCollection instance;

    private static final Map<Long, Map<Long, SavedCommand>> latestCommands = new HashMap<>();

    private LatestCommandsCollection() {
    }

    public static synchronized LatestCommandsCollection getInstance() {
        if (instance == null) {
            instance = new LatestCommandsCollection();
        }
        return instance;
    }

    public synchronized SavedCommand getLatestCommand(Guild guild, User user) {
        if (latestCommands.containsKey(guild.getIdLong())) {
            return latestCommands.get(guild.getIdLong()).get(user.getIdLong());
        }
        return null;
    }

    public synchronized void saveLatestCommand(Guild guild, User user, Command command, String label, String[] args) {
        latestCommands.putIfAbsent(guild.getIdLong(), new HashMap<>());
        latestCommands.get(guild.getIdLong()).put(user.getIdLong(), new SavedCommand(command, label, args));
    }

    public static class SavedCommand {
        private final Command command;
        private final String label;
        private final String[] args;

        SavedCommand(Command command, String label, String[] args) {
            this.command = command;
            this.label = label;
            this.args = args;
        }

        public Command getCommand() {
            return command;
        }

        public String getLabel() {
            return label;
        }

        public String[] getArgs() {
            return args;
        }
    }
}
