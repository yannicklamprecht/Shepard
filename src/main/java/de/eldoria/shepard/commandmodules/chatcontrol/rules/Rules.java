package de.eldoria.shepard.commandmodules.chatcontrol.rules;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.BiFunction;

public enum Rules {
    NORMAL_MESSAGE(checkNormal()),
    LINK_MESSAGE(checkNormal()),
    MAX_MENTIONS(checkNormal()),
    ATTACHMENT_MESSAGE(checkNormal()),
    CAPS_MESSAGES(checkNormal()),
    DOUBLE_MESSAGES(checkNormal()),
    BADWORD_MESSAGES(checkNormal()),
    REGEX_MESSAGES(checkNormal());

    private final BiFunction<Message, RuleSettings, Boolean> checkFunction;

    Rules(BiFunction<Message, RuleSettings, Boolean> checkFunction) {
        this.checkFunction = checkFunction;
    }

    public boolean isNotAllowed(Message message, RuleSettings ruleSettings) {
        return this.checkFunction.apply(message, ruleSettings);
    }

    public static BiFunction<Message, RuleSettings, Boolean> checkNormal() {
        return (message, rules) ->{
            return true;
        };
    }
}
