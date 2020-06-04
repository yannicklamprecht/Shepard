package de.eldoria.shepard.commandmodules.chatcontrol.rules;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.BiFunction;

public enum Rules {
    NORMAL_MESSAGE, LINK_MESSAGE;

    private final BiFunction<Message, RuleSettings, Boolean> checkFunction;

    Rules(BiFunction<Message, RuleSettings, Boolean> checkFunction) {

        this.checkFunction = checkFunction;
    }

    public boolean isNotAllowed(Message message, RuleSettings ruleSettings){
        return this.checkFunction(message, ruleSettings);
    }

}
