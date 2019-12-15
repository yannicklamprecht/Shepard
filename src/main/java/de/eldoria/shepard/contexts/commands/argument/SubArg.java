package de.eldoria.shepard.contexts.commands.argument;

public class SubArg {
    private final String argumentName;
    private final String localeTag;
    private final boolean isSubCommand;
    private String shortCommand;

    /**
     * Creates a new argument.
     *
     * @param argumentName name of the argument or subCommand
     * @param localeTag    locale code for localization
     */
    public SubArg(String argumentName, String localeTag) {
        this.argumentName = argumentName;
        this.localeTag = localeTag;
        this.isSubCommand = false;
    }

    /**
     * Creates a new argument or subcommand.
     *
     * @param argumentName name of the argument or subCommand
     * @param localeTag    locale code for localization
     * @param isSubCommand true if the command is a sub command.
     */
    public SubArg(String argumentName, String localeTag, boolean isSubCommand) {
        this.argumentName = argumentName;
        this.localeTag = localeTag;
        this.isSubCommand = isSubCommand;
    }

    /**
     * Get the argument name. Can be the subCommand or the argument name.
     *
     * @return argument name or subCommand
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * Returns the argument description in format:
     * If argument: "[argument] -> [description]"
     * If subcommand: "[argument] | [shortCommand] -> [description]".
     *
     * @return Argument description.
     */
    public String getArgumentDesc() {
        if (!isSubCommand) {
            return "**" + argumentName + "** -> " + localeTag;
        }

        return "**" + getCommandString() + "** -> " + localeTag;
    }

    /**
     * Get the short command of the subcommand.
     *
     * @return short command or null if it is not a sub command.
     */
    public String getShortCommand() {
        return shortCommand;
    }

    private void setShortCommand(String shortCommand) {
        this.shortCommand = shortCommand;
    }

    private String getCommandString() {
        return argumentName + " | " + shortCommand.toLowerCase();
    }

    /**
     * Returns true if the cmd matches the argument name or the short command.
     *
     * @param cmd command to test
     * @return true if a match is found. Always false if arg is not a command.
     */
    public boolean isSubCommand(String cmd) {
        return isSubCommand && (cmd.equalsIgnoreCase(argumentName) || cmd.equalsIgnoreCase(shortCommand));
    }

    /**
     * Checks if the argument is a subcommand.
     *
     * @return true if it is a subcommand
     */
    public boolean isSubCommand() {
        return isSubCommand;
    }

    /**
     * Generate the short command with additional length.
     *
     * @param additionalLength length of short command.
     */
    public void generateShortCommand(int additionalLength) {
        char[] argumentName = getArgumentName().toCharArray();
        StringBuilder shortCommand = new StringBuilder();
        shortCommand.append(argumentName[0]);
        for (int i = 0; i < additionalLength; i++) {
            shortCommand.append(argumentName[i + 1]);
        }
        for (char c : argumentName) {
            if (Character.isUpperCase(c)) {
                shortCommand.append(c);
            }
        }
        setShortCommand(shortCommand.toString());
    }

    /**
     * Get the locale tag of the argument.
     * @return locale tag as string
     */
    public String getLocaleTag() {
        return localeTag;
    }
}
