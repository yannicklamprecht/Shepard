package de.chojo.shepard.modules.commands;

public class CommandArg {
    private String argName;
    private String argDesc;
    private Boolean required;

    public CommandArg(String argName, String argDesc, Boolean required) {
        this.argName = argName;
        this.argDesc = argDesc;
        this.required = required;
    }

    private CommandArg() {
    }

    public String getArgName() {
        return argName;
    }

    public String getArgDesc() {
        return argDesc;
    }

    public Boolean getRequired() {
        return required;
    }
}

