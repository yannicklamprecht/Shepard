package de.eldoria.shepard.contexts.commands.argument;

public class SubArg {
    private String shortCommand;
    private String argumentName;
    private String localeCode;
    private boolean isSubCommand;

    /**
     * Creates a new argument.
     *
     * @param argumentName name of the argument or subCommand
     * @param localeCode   locale code for localization
     */
    public SubArg(String argumentName, String localeCode) {
        this.argumentName = argumentName;
        this.localeCode = localeCode;
        this.isSubCommand = false;
    }

    /**
     * Creates a new argument or subcommand.
     *
     * @param argumentName name of the argument or subCommand
     * @param localeCode   locale code for localization
     * @param isSubCommand true if the command is a sub command.
     */
    public SubArg(String argumentName, String localeCode, boolean isSubCommand) {
        this.argumentName = argumentName;
        this.localeCode = localeCode;
        this.isSubCommand = isSubCommand;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getArgumentDesc() {
        if (!isSubCommand) {
            return argumentName + " -> " + localeCode;
        }
        //TODO: COMMAND DESCRIPTION
        return "";
    }
}
