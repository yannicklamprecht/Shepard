package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.util.reactions.Emoji;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class SendCommandHelp extends Action {

    private final Command command;

    /**
     * Creates a new send command help action.
     *
     * @param command command for which the help should be send.
     */
    public SendCommandHelp(Command command) {
        super(Emoji.QUESTION_MARK, null, 60, false);
        this.command = command;
    }

    @Override
    protected void internalExecute(GuildMessageReactionAddEvent event) {
        command.sendCommandUsage(event.getChannel());
    }
}
