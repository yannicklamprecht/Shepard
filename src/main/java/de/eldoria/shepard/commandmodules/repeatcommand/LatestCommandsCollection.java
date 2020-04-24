package de.eldoria.shepard.commandmodules.repeatcommand;

import de.eldoria.shepard.commandmodules.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches the last used {@link Command} for a {@link User} on a {@link Guild} as a {@link SavedCommand}.
 */
public final class LatestCommandsCollection {
    private static final Map<Long, Map<Long, SavedCommand>> latestCommands = new HashMap<>();

    /**
     * Create a new latest command collection.
     */
    public LatestCommandsCollection() {
    }

    /**
     * Get the latest command of a user on a guild.
     *
     * @param guild guild for lookup
     * @param user  user for lookup
     * @return Saved Command or null if no command was found.
     */
    public synchronized SavedCommand getLatestCommand(Guild guild, User user) {
        if (latestCommands.containsKey(guild.getIdLong())) {
            return latestCommands.get(guild.getIdLong()).get(user.getIdLong());
        }
        return null;
    }

    /**
     * Save the latest command.
     *
     * @param guild   guild for saving
     * @param user    user for saving
     * @param command command which should be saved
     * @param label   label of command
     * @param args    command arguments
     */
    public synchronized void saveLatestCommand(Guild guild, User user, Command command, String label, String[] args) {
        if (command.getClass().getSimpleName().equals(RepeatCommand.class.getSimpleName())) return;
        latestCommands.putIfAbsent(guild.getIdLong(), new HashMap<>());
        latestCommands.get(guild.getIdLong()).put(user.getIdLong(), new SavedCommand(command, label, args));
    }

    public static final class SavedCommand {
        private final Command command;
        private final String label;
        private final String[] args;

        private SavedCommand(Command command, String label, String[] args) {
            this.command = command;
            this.label = label;
            this.args = args;
        }

        /**
         * Get the command instance.
         *
         * @return command instance
         */
        public Command getCommand() {
            return command;
        }

        /**
         * Get the command label.
         *
         * @return command label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Get the command args.
         *
         * @return command args
         */
        public String[] getArgs() {
            return args;
        }
    }
}
