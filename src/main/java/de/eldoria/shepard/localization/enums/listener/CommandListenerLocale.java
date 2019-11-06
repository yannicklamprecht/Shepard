package de.eldoria.shepard.localization.enums.listener;

public enum CommandListenerLocale {
    /**
     * Localization key for message bot answer.
     */
    M_BOT_ANSWER("listener.commandListener.message.botAnswer"),
    /**
     * Localization key for message insufficient permission.
     */
    M_INSUFFICIENT_PERMISSION("listener.commandListener.message.insufficientPermission"),
    /**
     * Localization key for message command not found.
     */
    M_COMMAND_NOT_FOUND("listener.commandListener.message.commandNotFound"),
    /**
     * Localization key for message suggestion.
     */
    M_SUGGESTION("listener.commandListener.message.suggestion"),
    /**
     * Localization key for message help command.
     */
    M_HELP_COMMAND("listener.commandListener.message.helpCommand");

    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    CommandListenerLocale(String localeCode) {
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
