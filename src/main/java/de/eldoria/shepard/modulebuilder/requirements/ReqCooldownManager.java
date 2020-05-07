package de.eldoria.shepard.modulebuilder.requirements;

import de.eldoria.shepard.basemodules.commanddispatching.CooldownManager;

public interface ReqCooldownManager {
    /**
     * Add the cooldown manager to an object.
     * @param cooldownManager cooldown manager isntance
     */
    void addCooldownManager(CooldownManager cooldownManager);
}
