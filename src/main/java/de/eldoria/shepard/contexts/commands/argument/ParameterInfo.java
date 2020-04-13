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

    /**
     * Create a new parameter info.
     *
     * @param command true if parameter is a command
     * @param commandName name of command
     * @param shortCommand short command
     * @param inputName name of input
     * @param inputDescription description of input
     * @param required true if command is required
     */
    public ParameterInfo(boolean command, String commandName, String shortCommand, String inputName, String inputDescription, boolean required) {
        this.command = command;
        this.commandName = commandName;
        this.shortCommand = shortCommand;
        this.inputName = TextLocalizer.localizeAllAndReplace(inputName, null);
        this.inputDescription = TextLocalizer.localizeAllAndReplace(inputDescription, null);
        this.required = required;
    }
}
