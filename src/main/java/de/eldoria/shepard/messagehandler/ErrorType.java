package de.eldoria.shepard.messagehandler;

public enum ErrorType {
    /**
     * Used when a database error occurs.
     */
    DATABASE_ERROR("Ups. Looks like my Database has a small hickup." + System.lineSeparator()
            + "Can you give me another try, pls?" + System.lineSeparator()
            + "IF error persists contact Bot Support"),
    /**
     * Used when a command argument is not valid.
     */
    INVALID_ARGUMENT("Invalid argument!"),
    /**
     * Used when a command action is not valid.
     */
    INVALID_ACTION("Invalid action!"),
    /**
     * Used when too many arguments are passed.
     */
    TOO_MANY_ARGUMENTS("Too many arguments!"),
    /**
     * Used when too few arguments are passed.
     */
    TOO_FEW_ARGUMENTS("Too few arguments!"),
    /**
     * Used when a number parse failed.
     */
    NOT_A_NUMBER("This is not a number!"),
    /**
     * Used when a channel is not a guild text channel.
     */
    NOT_GUILD_TEXT_CHANNEL("This is not a guild text channel!"),
    /**
     * Used when a category was passed but not found.
     */
    INVALID_CATEGORY("This is not a valid category!"),
    /**
     * Used when a channel was passed but not found.
     */
    INVALID_CHANNEL("This is not a valid channel!"),
    /**
     * Used when a role was passed but not found.
     */
    INVALID_ROLE("This is not a valid role!"),
    /**
     * Used when a user was passed but not found.
     */
    INVALID_USER("This is not a valid user!"),
    /**
     * Used when a id was found but the id is out of range.
     */
    INVALID_ID("This is not a valid id!"),
    /**
     * Used when the prefix is to long.
     */
    INVALID_PREFIX_LENGTH("Invalid prefix length. Only one or two Chars are allowed as prefix!"),
    /**
     * Used when a context was not found while command parsing.
     */
    INVALID_CONTEXT("Invalid Context!"),
    /**
     * Used when a boolean could not be parsed.
     */
    INVALID_BOOLEAN("Invalid input! Only 'true' and 'false' are valid inputs!"),
    /**
     * Used when the list type could not be parsed.
     */
    INVALID_LIST_TYPE("Invalid Input. Only 'blacklist' or 'whitelist are valid inputs"),
    /**
     * Used when no message was found.
     */
    NO_MESSAGE_FOUND("No message found!"),
    /**
     * Used when no invite was found while registering.
     */
    NO_INVITE_FOUND("No invite found with this code!"),
    /**
     * Used when no quote was found.
     */
    NO_QUOTE_FOUND("No quote found!"),
    /**
     * Used when a ticket type is already defined.
     */
    TYPE_ALREADY_DEFINED("This type is already defined!"),
    /**
     * Used when a ticket type is not found.
     */
    TYPE_NOT_FOUND("Ticket type not found!"),
    /**
     * Used when someone tries to open a ticket for himself.
     */
    TICKET_SELF_ASSIGNMENT("You can't open a ticket for yourself!"),
    /**
     * Used when a ticket close command is executed in a non ticket channel.
     */
    NOT_TICKET_CHANEL("This is not a ticket channel!"),
    /**
     * Used when a context is not found.
     */
    CONTEXT_NOT_FOUND("Context not found. Please use the context name or an alias!");


    /**
     * Get the error message of the error type.
     */
    public final String message;

    private ErrorType(String message) {
        this.message = message;
    }
}
