package de.eldoria.shepard.messagehandler;

public enum ErrorType {
    /**
     * Used for general error pasting.
     */
    GENERAL("error.general"),
    /**
     * Used when a database error occurs.
     */
    DATABASE_ERROR("error.databaseError", true),
    /**
     * Used when a database error occurs.
     */
    INTERNAL_ERROR("error.internalError", true),
    /**
     * Used when a (web) service isn't available at the moment, e.g. http response code 4xx or 5xx.
     */
    SERVICE_UNAVAILABLE("error.serviceUnavailable"),
    /**
     * Used when a command argument is not valid.
     */
    INVALID_ARGUMENT("error.invalidArgument"),
    /**
     * Used when a command action is not valid.
     */
    INVALID_ACTION("error.invalidAction"),
    /**
     * Used when too many arguments are passed.
     */
    TOO_MANY_ARGUMENTS("error.tooManyArguments"),
    /**
     * Used when too few arguments are passed.
     */
    TOO_FEW_ARGUMENTS("error.tooFewArguments"),
    /**
     * Used when a number parse failed.
     */
    NOT_A_NUMBER("error.notANumber"),
    /**
     * Used when the user has not enough Kudos.
     */
    NOT_ENOUGH_KUDOS("error.notEnoughKudos"),
    /**
     * Used when a channel is not a guild text channel.
     */
    NOT_GUILD_TEXT_CHANNEL("error.notGuildTextChannel"),
    /**
     * Used when a category was passed but not found.
     */
    INVALID_CATEGORY("error.invalidCategory"),
    /**
     * Used when a channel was passed but not found.
     */
    INVALID_CHANNEL("error.invalidChannel"),
    /**
     * Used when a role was passed but not found.
     */
    INVALID_ROLE("error.invalidRole"),
    /**
     * Used when a user was passed but not found.
     */
    INVALID_USER("error.invalidUser"),
    /**
     * Used when a id was found but the id is out of range.
     */
    INVALID_ID("error.invalidId"),
    /**
     * Used when the prefix is to long.
     */
    INVALID_PREFIX_LENGTH("error.invalidPrefixLength"),
    /**
     * Used when a context was not found while command parsing.
     */
    INVALID_CONTEXT("error.invalidContext"),
    /**
     * Used when a boolean could not be parsed.
     */
    INVALID_BOOLEAN("error.invalidBoolean"),
    /**
     * Used when the list type could not be parsed.
     */
    INVALID_LIST_TYPE("error.invalidListType"),
    /**
     * Used when the list type could not be parsed.
     */
    INVALID_ADDRESS("error.invalidAddress"),
    /**
     * Used when a time doesn't have a valid time formatting.
     */
    INVALID_TIME("error.invalidTime"),
    /**
     * Used when a time doesn't have a valid time formatting.
     */
    INVALID_LOCALE_CODE("error.invalidTime"),
    /**
     * Used when no message was found.
     */
    NO_MESSAGE_FOUND("error.noMessageFound"),
    /**
     * Used when no invite was found while registering.
     */
    NO_INVITE_FOUND("error.noInviteFound"),
    /**
     * Used when no quote was found.
     */
    NO_QUOTE_FOUND("error.noQuoteFound"),
    /**
     * Used when a ticket type is already defined.
     */
    TYPE_ALREADY_DEFINED("error.typeAlreadyDefined"),
    /**
     * Used when a ticket type is not found.
     */
    TYPE_NOT_FOUND("error.typeNotFound"),
    /**
     * Used when someone tries to open a ticket for himself.
     */
    TICKET_SELF_ASSIGNMENT("error.ticketSelfAssignment"),
    /**
     * Used when a ticket close command is executed in a non ticket channel.
     */
    NOT_TICKET_CHANNEL("error.notTicketChannel"),
    /**
     * Used when a context is not found.
     */
    CONTEXT_NOT_FOUND("error.contextNotFound"),
    /**
     * Used when no emote was found.
     */
    NO_EMOTE_FOUND("error.noEmoteFound"),
    /**
     * Used when a user executes a command on himself, when he is not allowed to do it!.
     */
    SELF_ASSIGNMENT("error.selfAssignment"),
    /**
     * Used when no last command was found.
     */
    NO_LAST_COMMAND_FOUND("error.noLastCommandFound"),
    /**
     * Used when no guess game image was found.
     */
    INVALID_IMAGE_URL("error.invalidImageUrl"),
    /**
     * Used when there is no defined ticket type on a guild.
     */
    NO_TICKET_TYPES_DEFINED("error.noTicketTypesDefined"),
    /**
     * Used when a command which is restricted to a specific channel is executed outside the channel.
     */
    EXCLUSIVE_CHANNEL("error.exclusiveChannel"),
    /**
     * Used when a argument help is executed on a command without arguments.
     */
    NO_ARGUMENT_FOUND("error.noArgumentFound"),
    /**
     * Used when a command was not found.
     */
    COMMAND_NOT_FOUND("error.commandNotFound");


    /**
     * Get the error message of the error type.
     */
    public final String taggedMessage;

    /**
     * True if the error should be send as an embed.
     */
    public final boolean isEmbed;

    /**
     * Create a new error type.
     *
     * @param tag   locale tag for error
     * @param embed true if error should be send as embed
     */
    ErrorType(String tag, boolean embed) {
        if (embed) {
            this.taggedMessage = "$" + tag + "$";
        } else {
            this.taggedMessage = "**ERROR**" + System.lineSeparator() + "$" + tag + "$";
        }
        this.isEmbed = embed;
    }

    /**
     * Create a new error type. Default not embed.
     * @param tag locale tag for error
     */
    ErrorType(String tag) {
        this(tag, false);
    }
}
