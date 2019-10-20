package de.eldoria.shepard.collections;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class LatestCommandsCollection {
    private static LatestCommandsCollection instance;

    private static Map<Long, Map<Long, SavedCommand>> latestCommands = new HashMap<>();

    private LatestCommandsCollection() {
    }

    public static synchronized LatestCommandsCollection getInstance() {
        if (instance == null) {
            instance = new LatestCommandsCollection();
        }
        return instance;
    }

    public SavedCommand getLatestCommand(Guild guild, User user) {
        if (latestCommands.containsKey(guild.getOwnerIdLong())) {
            return latestCommands.get(guild.getOwnerIdLong()).get(user.getIdLong());
        }
        return null;
    }

    public void saveLatestCommand(Guild guild, User user, Command command, String label, String[] args) {
        latestCommands.putIfAbsent(guild.getOwnerIdLong(), new HashMap<>());
        latestCommands.get(guild.getOwnerIdLong()).put(user.getIdLong(), new SavedCommand(command, label, args));
    }

    public static class SavedCommand {
        private Command command;
        private String label;
        private String[] args;

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
