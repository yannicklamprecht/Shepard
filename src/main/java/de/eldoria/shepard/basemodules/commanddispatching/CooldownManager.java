package de.eldoria.shepard.basemodules.commanddispatching;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Cooldown Manager.
 * Saves a cooldown for a user. Removes invalid cooldowns every 10 minutes.
 * Cooldowns are user and not guild restricted.
 */
public final class CooldownManager implements Runnable, ReqDataSource {
    private final Map<Command, Map<Long, LocalDateTime>> guildCooldown = new HashMap<>();
    private final Map<Command, Map<Long, LocalDateTime>> userCooldown = new HashMap<>();
    private CommandData commandData;

    /**
     * Create a new cooldown manager.
     */
    public CooldownManager() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Check if the command is not in cooldown by the user and on the guild.
     *
     * @param command command to check the cooldown
     * @param wrapper wrapper for cooldown check
     * @return the cooldown in seconds or 0 if there is no cooldown.
     */
    public int getCurrentCooldown(Command command, EventWrapper wrapper) {
        long cooldown;
        if (wrapper.isGuildEvent()) {
            cooldown = Math.max(getGuildCooldown(command, wrapper.getGuild().get()),
                    getUserCooldown(command, wrapper.getAuthor()));
        } else {
            cooldown = getUserCooldown(command, wrapper.getAuthor());
        }

        if (cooldown == 0) {
            return 0;
        }
        return (int) (cooldown);
    }

    /**
     * Restarts the cooldown for the command.
     *
     * @param command command for cooldown
     * @param wrapper wrapper for cooldown check
     */
    public void renewCooldown(Command command, EventWrapper wrapper) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime userCooldownTime = now.plusSeconds(commandData.getUserCooldown(command));
        userCooldown.putIfAbsent(command, new HashMap<>());
        userCooldown.get(command).put(wrapper.getAuthor().getIdLong(), userCooldownTime);

        if (wrapper.isGuildEvent()) {
            LocalDateTime guildCooldownTime = now.plusSeconds(commandData.getGuildCooldown(command));
            guildCooldown.putIfAbsent(command, new HashMap<>());
            guildCooldown.get(command).put(wrapper.getGuild().get().getIdLong(), guildCooldownTime);
        }

    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        userCooldown.values().forEach(c -> c.entrySet().stream().filter(e -> e.getValue().isBefore(now))
                .map(Map.Entry::getKey).collect(Collectors.toList()).forEach(c::remove));
        guildCooldown.values().forEach(c -> c.entrySet().stream().filter(e -> e.getValue().isBefore(now))
                .map(Map.Entry::getKey).collect(Collectors.toList()).forEach(c::remove));
    }

    private long getUserCooldown(Command command, User user) {
        if (!userCooldown.containsKey(command)) {
            return 0;
        }
        Map<Long, LocalDateTime> map = userCooldown.get(command);
        return getCooldown(map.get(user.getIdLong()));
    }

    private long getGuildCooldown(Command command, Guild guild) {
        if (!guildCooldown.containsKey(command)) {
            return 0;
        }
        Map<Long, LocalDateTime> map = guildCooldown.get(command);

        return getCooldown(map.get(guild.getIdLong()));
    }

    private long getCooldown(LocalDateTime time) {
        if (time == null) {
            return 0;
        }
        long seconds = LocalDateTime.now().until(time, ChronoUnit.SECONDS);

        return Math.max(seconds, 0);
    }

    @Override
    public void addDataSource(DataSource source) {
        commandData = new CommandData(source);
    }
}
