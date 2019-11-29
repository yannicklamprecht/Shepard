package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.database.types.ContextSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class CooldownManager implements Runnable {
    private static CooldownManager instance;

    private Map<Command, Map<Long, LocalDateTime>> guildCooldown = new HashMap<>();
    private Map<Command, Map<Long, LocalDateTime>> userCooldown = new HashMap<>();

    private CooldownManager() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Get the current cooldown manager instance.
     *
     * @return the cooldown manager instance
     */
    public static synchronized CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }

    /**
     * Check if the command is not in cooldown by the user and on the guild.
     * Triggers a new cooldown if the command can be executed.
     *
     * @param command command to check the cooldown
     * @param user    user to check
     * @param guild   guild to check
     * @return the cooldown in seconds or 0 if there is no cooldown.
     */
    public float getCurrentCooldown(Command command, Guild guild, User user) {
        ContextSettings contextData = command.getContextData();
        if (!contextData.hasCooldown()) {
            return 0;
        }

        long cooldown = Math.max(getGuildCooldown(command, guild), getUserCooldown(command, user));
        if (cooldown == 0) {
            return 0;
        }
        return (int) (cooldown);
    }

    private long getUserCooldown(Command command, User user) {
        if (!command.getContextData().hasUserCooldown() || !userCooldown.containsKey(command)) {
            return 0;
        }
        Map<Long, LocalDateTime> map = userCooldown.get(command);
        return getCooldown(map.get(user.getIdLong()));
    }

    private long getGuildCooldown(Command command, Guild guild) {
        if (!command.getContextData().hasGuildCooldown() || !guildCooldown.containsKey(command)) {
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

    /**
     * Restarts the cooldown for the command.
     *
     * @param command command for cooldown
     * @param guild   guild for cooldown
     * @param user    user for cooldown
     */
    public void renewCooldown(Command command, Guild guild, User user) {
        ContextSettings contextData = command.getContextData();
        if (!contextData.hasCooldown()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        if (contextData.hasUserCooldown()) {
            LocalDateTime userCooldownTime = now.plusSeconds(contextData.getUserCooldown());
            userCooldown.putIfAbsent(command, new HashMap<>());
            userCooldown.get(command).put(user.getIdLong(), userCooldownTime);
        }
        if (contextData.hasGuildCooldown()) {
            LocalDateTime guildCooldownTime = now.plusSeconds(contextData.getGuildCooldown());
            guildCooldown.putIfAbsent(command, new HashMap<>());
            guildCooldown.get(command).put(guild.getIdLong(), guildCooldownTime);
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
}
