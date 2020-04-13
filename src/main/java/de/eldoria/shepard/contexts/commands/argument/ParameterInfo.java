package de.eldoria.shepard.contexts.commands.argument;

import de.eldoria.shepard.localization.util.TextLocalizer;
import lombok.Getter;

@Getter
public class ParameterInfo {
    private String shortCommand;
    private String inputName;
    private String inputDescription;
    private boolean required;
    private boolean command;
    private String commandName;

    public ParameterInfo(boolean command, String commandName, String shortCommand, String inputName, String inputDescription, boolean required) {
        this.command = command;
        this.commandName = commandName;
        this.shortCommand = shortCommand;
        this.inputName = TextLocalizer.localizeAllAndReplace(inputName, null);
        this.inputDescription = TextLocalizer.localizeAllAndReplace(inputDescription, null);
        this.required = required;
    }
}
