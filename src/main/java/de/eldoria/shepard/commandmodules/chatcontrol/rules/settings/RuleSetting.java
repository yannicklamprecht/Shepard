package de.eldoria.shepard.commandmodules.chatcontrol.rules.settings;

import lombok.Getter;

@Getter
public class RuleSetting<T> {
    private final RuleState state;
    private final T value;

    public RuleSetting(RuleState state, T value) {
        this.state = state;
        this.value = value;
    }
}
