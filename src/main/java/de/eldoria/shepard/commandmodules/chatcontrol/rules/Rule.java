package de.eldoria.shepard.commandmodules.chatcontrol.rules;

import net.dv8tion.jda.api.entities.Message;

public interface Rule {
    /**
     * Checks if a message should be denied or not.
     *
     * @param message  message to check
     * @param settings settings for this rule.
     * @return true if the message is not allowed;
     */
    public boolean isNotAllowed(Message message, RuleSettings settings);
}
